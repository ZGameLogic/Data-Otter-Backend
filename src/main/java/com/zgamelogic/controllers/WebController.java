package com.zgamelogic.controllers;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.zgamelogic.data.serializable.*;
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
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@RestController
@Slf4j
public class WebController {

    private final static int HOURS_TO_KEEP = 12;
    private final static int NON_EXTENDED_HOURS = 8;
    private static final String MONITORS_CONFIG = "monitors.json";
    private static final String HISTORY_DIR = "history";

    private HashMap<String, Class> classMap;

    @PostConstruct
    private void init(){
        classMap = new HashMap<>();
        classMap.put("api", APIMonitor.class);
        classMap.put("minecraft", MinecraftMonitor.class);
        classMap.put("web", WebMonitor.class);
        classMap.put("api_history", Status[].class);
        classMap.put("web_history", Status[].class);
        classMap.put("minecraft_history", StatusMinecraft[].class);
        updateAllMonitorStatusHistory();
    }

    @GetMapping("monitors")
    private LinkedList<Monitor> getMonitors(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) Boolean history,
            @RequestParam(required = false) Boolean extended
    ){
        LinkedList<Monitor> monitors = loadMonitors();

        if(history == null) history = false;
        if(extended == null) extended = false;
        if(id != null){ monitors.removeIf(m -> m.getId() != id); }
        for(Monitor m: monitors){
            m.setStatus(loadMonitorHistory(m, history, extended));
        }


        return monitors;
    }

    @Scheduled(cron = "0 */1 * * * *")
    private void oneMinuteTask() {
        updateAllMonitorStatusHistory();
    }

    private void updateAllMonitorStatusHistory() {
        loadMonitors().forEach(monitor ->
                new Thread(() -> updateMonitorStatusHistory(monitor)).start()
        );
    }

    /**
     * Gets the new current data for a monitor and saves it to its history
     * @param monitor Monitor to get the new data for
     */
    private void updateMonitorStatusHistory(Monitor monitor){
        LinkedList<Status> historyData = loadMonitorHistory(monitor, true, true);
        Status status = runMonitorCheck(monitor);
        historyData.add(status);
        historyData.sort(Comparator.comparing(Status::getTaken).reversed());
        Date xHoursAgo = Date.from(LocalDateTime.now().minusHours(HOURS_TO_KEEP).toInstant(ZoneOffset.ofHours(0)));
        historyData.removeIf(h -> h.getTaken() == null || h.getTaken().before(xHoursAgo));
        saveMonitorHistory(monitor, historyData);
    }

    /**
     * Loads the history for a monitor from disk
     * @param monitor Monitor to load the history for
     * @return Linked list of history objects for the monitor
     */
    private LinkedList<Status> loadMonitorHistory(Monitor monitor, boolean includeHistory, boolean extended){
        File historyFile = new File(HISTORY_DIR + "/" + monitor.getId() + ".json");
        try {
            ObjectMapper om = new ObjectMapper();
            if(includeHistory || extended) {
                LinkedList<Status> historyData = new LinkedList<>(Arrays.asList((Status[]) om.readValue(historyFile, classMap.get(monitor.getType() + "_history"))));
                Date xHoursAgo = Date.from(LocalDateTime.now().minusHours(extended ? HOURS_TO_KEEP: NON_EXTENDED_HOURS).toInstant(ZoneOffset.ofHours(0)));
                historyData.removeIf(h -> h.getTaken() == null || h.getTaken().before(xHoursAgo));
                return historyData;
            } else {
                LinkedList<Status> status = new LinkedList<>(Arrays.asList((Status[]) om.readValue(historyFile, classMap.get(monitor.getType() + "_history"))));
                return new LinkedList<>(Collections.singletonList(status.getFirst()));
            }
        } catch (Exception e){
            return new LinkedList<>();
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
