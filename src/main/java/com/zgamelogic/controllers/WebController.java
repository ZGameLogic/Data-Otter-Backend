package com.zgamelogic.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.zgamelogic.data.serializable.*;
import com.zgamelogic.data.serializable.events.Event;
import com.zgamelogic.data.serializable.monitors.APIMonitor;
import com.zgamelogic.data.serializable.monitors.MinecraftMonitor;
import com.zgamelogic.data.serializable.monitors.Monitor;
import com.zgamelogic.data.serializable.monitors.WebMonitor;
import com.zgamelogic.helpers.APIInterfacer;
import com.zgamelogic.helpers.MCInterfacer;
import com.zgamelogic.helpers.WebInterfacer;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@RestController
@Slf4j
public class WebController {

    private final static int HOURS_TO_KEEP = 12;
    private final static int NON_EXTENDED_HOURS = 8;
    private final static int EVENT_COMBINE_MINUTE_THRESHOLD = 8;
    private final static int MINUTES_TO_COMBINE_EVENTS = 60;

    private final static int ALERT_THRESHOLD = 1;

    private static final String MONITORS_CONFIG = "monitors.json";
    private static final String HISTORY_DIR = "history";
    private static final String EVENTS_DIR = "events";

    private HashMap<String, Class> classMap;

    private HashMap<Integer, LinkedList<Status>> reports;

    @PostConstruct
    private void init(){
        classMap = new HashMap<>();
        classMap.put("api", APIMonitor.class);
        classMap.put("minecraft", MinecraftMonitor.class);
        classMap.put("web", WebMonitor.class);
        classMap.put("api_history", Status[].class);
        classMap.put("web_history", Status[].class);
        classMap.put("minecraft_history", StatusMinecraft[].class);
        reports = new HashMap<>();
    }

    @GetMapping("monitors")
    private LinkedList<Monitor> getMonitors(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) boolean history,
            @RequestParam(required = false) boolean extended,
            @RequestParam(required = false) boolean uncondensed
    ){
        LinkedList<Monitor> monitors = loadMonitors();

        if(id != null){ monitors.removeIf(m -> m.getId() != id); }

        for (Monitor m : monitors) {
            m.setStatus(loadMonitorHistory(m, history, extended, uncondensed));
        }

        return monitors;
    }

    @GetMapping("events")
    private LinkedList<Event> getEvents(
            @RequestParam(required = false) Long startDate,
            @RequestParam(required = false) Long endDate
    ){
        if(startDate != null && endDate != null){
            return loadEvents(new Date(startDate), new Date(endDate));
        } else if(startDate != null){
            return loadEvents(new Date(startDate));
        } else {
            return loadEvents();
        }
    }

    @PostMapping("/node/report")
    private void nodeReport(@RequestBody String body) throws JsonProcessingException {
        JSONArray jsonBody = new JSONArray(body);
        LinkedList<Monitor> monitors = new LinkedList<>();
        ObjectMapper om = new ObjectMapper();
        for(int i = 0; i < jsonBody.length(); i++){
            JSONObject monitor = jsonBody.getJSONObject(i);
            Monitor m = (Monitor) om.readValue(monitor.toString(), classMap.get(monitor.getString("type")));
            if(monitor.getString("type").equals("minecraft")){
                LinkedList<Status> s = new LinkedList<>(Arrays.asList(om.readValue(monitor.getJSONArray("status").toString(), StatusMinecraft[].class)));
                m.setStatus(s);
            }
            monitors.add(m);
        }
        addReport(monitors);
    }

    @Scheduled(cron = "58 */1 * * * *")
    private void preOneMinuteTask(){
        reports = new HashMap<>();
    }

    @Scheduled(cron = "30 */1 * * * *")
    private void postOneMinuteTask(){
        LinkedList<Monitor> monitors = loadMonitors();
        reports.forEach((key, statuses) -> {
            Status masterStatus = combineStatuses(statuses);
            for(Monitor m: monitors){
                if(m.getId() == key){
                    m.addMonitorStatus(masterStatus);
                    break;
                }
            }
        });
        updateAllMonitorStatusHistory(monitors);
    }

    @Scheduled(cron = "0 */1 * * * *")
    private void oneMinuteTask() {
        addReport(pingMonitors());
    }

    private synchronized void addReport(LinkedList<Monitor> monitors){
        for(Monitor monitor: monitors){
            if(reports.containsKey(monitor.getId())){
                reports.get(monitor.getId()).addAll(monitor.getStatus());
            } else {
                reports.put(monitor.getId(), monitor.getStatus());
            }
        }
    }

    private Status combineStatuses(LinkedList<Status> statuses){
        LinkedList<Status> goodStatuses = (LinkedList<Status>) statuses.clone();
        goodStatuses.removeIf(status -> !status.isStatus());

        LinkedList<Status> badStatuses = (LinkedList<Status>) statuses.clone();
        badStatuses.removeIf(Status::isStatus);

        if(badStatuses.size() >= ALERT_THRESHOLD){
            LinkedList<String> nodesReported = new LinkedList<>();
            badStatuses.forEach(status -> {
                if(status.getNodes() != null) {
                    nodesReported.addAll(status.getNodes());
                }
            });
            Status masterStatus = badStatuses.getFirst();
            masterStatus.setNodes(nodesReported);
            return masterStatus;
        } else {
            LinkedList<String> nodesReported = new LinkedList<>();
            goodStatuses.forEach(status -> {
                if (status.getNodes() != null) {
                    nodesReported.addAll(status.getNodes());
                }
            });
            Status masterStatus = goodStatuses.getFirst();
            masterStatus.setNodes(nodesReported);
            return masterStatus;
        }
    }

    private LinkedList<Monitor> pingMonitors(){
        LinkedList<Monitor> monitors = new LinkedList<>();
        LinkedList<Thread> threads = new LinkedList<>();
        loadMonitors().forEach(monitor -> {
            Thread thread = new Thread(() -> {
                Status monitorStatus = runMonitorCheck(monitor);
                monitorStatus.addNode("root");
                monitor.addMonitorStatus(monitorStatus);
                synchronized(this) {
                    monitors.add(monitor);
                }
            });
            threads.add(thread);
            thread.start();
        });
        while(!threads.isEmpty()) threads.removeIf(thread -> !thread.isAlive());
        return monitors;
    }

    private void updateAllMonitorStatusHistory(LinkedList<Monitor> monitors) {
        monitors.removeIf(monitor -> monitor.getStatus() == null || monitor.getStatus().isEmpty());
        monitors.forEach(monitor -> new Thread(() ->
            updateMonitorStatusHistory(monitor).ifPresent(this::saveEvent))
        .start());
    }

    /**
     * Gets the new current data for a monitor and saves it to its history
     * Creates and saves events if something is fishy
     * @param monitor Monitor to get the new data for
     */
    private Optional<Event> updateMonitorStatusHistory(Monitor monitor){
        LinkedList<Status> historyData = loadMonitorHistory(monitor, true, true, true);
        historyData.add(monitor.getStatus().getFirst());
        historyData.sort(Comparator.comparing(Status::getTaken).reversed());
        Date xHoursAgo = Date.from(LocalDateTime.now().minusHours(HOURS_TO_KEEP).toInstant(ZoneOffset.ofHours(0)));
        historyData.removeIf(h -> h.getTaken() == null || h.getTaken().before(xHoursAgo));
        saveMonitorHistory(monitor, historyData);
        // compare the last two entries
        if(historyData.size() >= 2) {
            if (historyData.get(0).isStatus() != historyData.get(1).isStatus()) {
                Event event = new Event(monitor);
                event.addEntry(new Event.Entry(historyData.get(0).isStatus()));
                return Optional.of(event);
            }
        }
        return Optional.empty();
    }

    /**
     * Loads the history for a monitor from disk
     * @param monitor Monitor to load the history for
     * @return Linked list of history objects for the monitor
     */
    private LinkedList<Status> loadMonitorHistory(Monitor monitor, boolean includeHistory, boolean extended, boolean uncondensed){
        File historyFile = new File(HISTORY_DIR + "/" + monitor.getId() + ".json");
        try {
            ObjectMapper om = new ObjectMapper();
            if(includeHistory || extended) {
                LinkedList<Status> historyData = new LinkedList<>(Arrays.asList((Status[]) om.readValue(historyFile, classMap.get(monitor.getType() + "_history"))));
                Date xHoursAgo = Date.from(LocalDateTime.now().minusHours(extended ? HOURS_TO_KEEP: NON_EXTENDED_HOURS).toInstant(ZoneOffset.ofHours(0)));
                historyData.removeIf(h -> h.getTaken() == null || h.getTaken().before(xHoursAgo));
                if(uncondensed) {
                    return historyData;
                } else {
                    return condense(historyData);
                }
            } else {
                LinkedList<Status> status = new LinkedList<>(Arrays.asList((Status[]) om.readValue(historyFile, classMap.get(monitor.getType() + "_history"))));
                return new LinkedList<>(Collections.singletonList(status.getFirst()));
            }
        } catch (Exception e){
            return new LinkedList<>();
        }
    }

    private LinkedList<Status> condense(LinkedList<Status> statuses){
        LinkedList<Status> condensed = new LinkedList<>();
        for (int i = 0; i < statuses.size(); i++) {
            Status status = statuses.get(i);
            if(i == 0 || i == statuses.size() - 1){
                condensed.add(status);
            } else if(!status.softEquals(statuses.get(i - 1))){
                if(!condensed.contains(statuses.get(i - 1))) condensed.add(statuses.get(i - 1));
                condensed.add(status);
            }
        }
        return condensed;
    }

    private void saveEvent(Event event){
        Optional<Event> foundEvent = findExistingEvent(event);
        foundEvent.ifPresent(value -> event.addEntries(value.getEntries()));
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy_HH-mm-ss");
        File eventsFile = new File(EVENTS_DIR + "/" + event.getMonitorId() + "_" + dateFormat.format(event.getStartTime()) + ".json");
        eventsFile.getParentFile().mkdirs();

        ObjectWriter writer = new ObjectMapper().writer(new DefaultPrettyPrinter());
        try {
            eventsFile.createNewFile();
            writer.writeValue(eventsFile, event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<Event> findExistingEvent(Event event){
        Calendar eventStartTime = Calendar.getInstance();
        eventStartTime.setTime(event.getStartTime());

        for(File file: new File(EVENTS_DIR).listFiles()){
            if(Long.parseLong(file.getName().split("_")[0]) != event.getMonitorId()) continue;
            Event currentEvent = loadEvent(file);
            if(currentEvent == null) continue;
            Calendar currentEventEndTime = Calendar.getInstance();
            currentEventEndTime.setTime(currentEvent.getEndTime());
            currentEventEndTime.add(Calendar.HOUR, 1);

            if(eventStartTime.before(currentEventEndTime)) {
                return Optional.of(currentEvent);
            }

        }
        return Optional.empty();
    }

    private LinkedList<Event> loadEvents(){
        return loadEvents(new Date());
    }

    private LinkedList<Event> loadEvents(Date start){
        return loadEvents(start, new Date());
    }

    private LinkedList<Event> loadEvents(Date start, Date end){
        Calendar startTime = Calendar.getInstance();
        startTime.setTime(start);
        startTime.set(Calendar.HOUR_OF_DAY, 0);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.SECOND, 0);

        Calendar endTime = Calendar.getInstance();
        endTime.setTime(end);
        endTime.set(Calendar.HOUR_OF_DAY, 23);
        endTime.set(Calendar.MINUTE, 59);
        endTime.set(Calendar.SECOND, 59);

        LinkedList<Event> eventsList = new LinkedList<>();

        File eventsDir = new File(EVENTS_DIR);
        if(eventsDir.exists()) {
            ObjectMapper om = new ObjectMapper();
            for(File file: eventsDir.listFiles()){
                Event event = loadEvent(file);
                if(event == null) continue;

                Calendar eventStartTime = Calendar.getInstance();
                eventStartTime.setTime(event.getStartTime());

                Calendar eventEndTime = Calendar.getInstance();
                eventStartTime.setTime(event.getEndTime());

                if((eventStartTime.after(startTime) && eventStartTime.before(endTime)) ||
                        (eventEndTime.after(startTime) && eventEndTime.before(endTime))) {
                    eventsList.add(event);
                }
            }
        }
        return eventsList;
    }

    private Event loadEvent(File file){
        ObjectMapper om = new ObjectMapper();
        try {
            return om.readValue(file, Event.class);
        } catch (IOException e) {
            log.error("Cannot read file: " + file.getName(), e);
            return null;
        }
    }

    private void saveMonitorHistory(Monitor monitor, LinkedList<Status> status){
        File historyFile = new File(HISTORY_DIR + "/" + monitor.getId() + ".json");
        if(!historyFile.exists()) {
            try {
                historyFile.getParentFile().mkdirs();
                historyFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        ObjectWriter writer = new ObjectMapper().writer(new DefaultPrettyPrinter());
        try {
            writer.writeValue(historyFile, status);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private LinkedList<Monitor> loadMonitors(){
        LinkedList<Monitor> monitors = new LinkedList<>();
        try {
            ObjectMapper om = new ObjectMapper();
            String jsonString = new String(Files.readAllBytes(Paths.get(MONITORS_CONFIG)));
            JSONArray json = new JSONArray(jsonString);
            for(int i = 0; i < json.length(); i++){
                JSONObject monitor = json.getJSONObject(i);
                Monitor m = (Monitor) om.readValue(monitor.toString(), classMap.get(monitor.getString("type")));
                monitors.add(m);
            }
        } catch (IOException e) {
            log.error("Error when loading monitors", e);
        }
        return monitors;
    }

    private Status runMonitorCheck(Monitor monitor) {
        switch(monitor.getType()){
            case "minecraft":
                return MCInterfacer.pingServer((MinecraftMonitor) monitor);
            case "api":
                return APIInterfacer.pingAPI((APIMonitor) monitor);
            case "web":
                return WebInterfacer.pingWeb((WebMonitor) monitor);
        }
        return null;
    }
}
