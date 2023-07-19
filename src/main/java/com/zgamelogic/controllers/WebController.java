package com.zgamelogic.controllers;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.zgamelogic.data.APIMonitor;
import com.zgamelogic.data.MinecraftMonitor;
import com.zgamelogic.data.Monitor;
import com.zgamelogic.data.WebMonitor;
import com.zgamelogic.helpers.APIInterfacer;
import com.zgamelogic.helpers.MCInterfacer;
import com.zgamelogic.helpers.WebInterfacer;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@RestController
@Slf4j
public class WebController {
    private HashMap<String, Class> classMap;
    private static final String PATH = "monitors.json";

    @PostConstruct
    private void init(){
        classMap = new HashMap<>();
        classMap.put("api", APIMonitor.class);
        classMap.put("minecraft", MinecraftMonitor.class);
        classMap.put("web", WebMonitor.class);
        classMap.put("api[]", APIMonitor[].class);
        classMap.put("minecraft[]", MinecraftMonitor[].class);
        classMap.put("web[]", WebMonitor[].class);
    }

    @GetMapping("monitors/**")
    private LinkedList<Monitor> getMonitors(HttpServletRequest request){
        String monitorId = request.getRequestURI().replaceFirst("monitors", "").replaceAll("/", "");
        if(monitorId.isEmpty()) {
            return getMonitorsStatus();
        }
        LinkedList<Monitor> monitors = new LinkedList<>();
        Monitor monitor = getMonitorStatus(Integer.parseInt(monitorId));
        if(monitor != null) monitors.add(monitor);
        return monitors;
    }

    @GetMapping("history/**")
    private LinkedList<Monitor> getMonitorHistory(HttpServletRequest request){
        String monitorId = request.getRequestURI().replaceFirst("history", "").replaceAll("/", "");
        return loadHistoryData(Integer.parseInt(monitorId));
    }

    @PostMapping("monitors")
    private String createMonitor(@RequestBody String body){
        try {
            ObjectMapper om = new ObjectMapper();
            JSONObject json = new JSONObject(body);
            String monitor = json.getString("type");
            Monitor m = (Monitor) om.readValue(json.toString(), classMap.get(monitor));
            saveNewMonitor(m);
            return "New monitor created";
        } catch (IOException e) {
            return "Error creating new monitor";
        }
    }

    @PostMapping("test")
    private Monitor testMonitor(@RequestBody String body){
        try {
            ObjectMapper om = new ObjectMapper();
            JSONObject json = new JSONObject(body);
            String monitor = json.getString("type");
            Monitor m = (Monitor) om.readValue(json.toString(), classMap.get(monitor));
            runMonitorCheck(m);
            return m;
        } catch (IOException e) {
            return null;
        }
    }

    @Scheduled(cron = "0 */5 * * * *")
    private void fiveMinuteTask() {
        log.info("Logging data");
        for(Monitor m: getMonitorsStatus()){
            saveMonitorData(m);
        }
    }

    private Monitor getMonitorStatus(int id){
        LinkedList<Monitor> monitors = loadMonitors();
        for(Monitor monitor: monitors){
            if(monitor.getId() == id){
                runMonitorCheck(monitor);
                return monitor;
            }
        }
        return null;
    }

    private LinkedList<Monitor> getMonitorsStatus(){
        LinkedList<Monitor> monitors = loadMonitors();
        for(Monitor monitor: monitors){
            runMonitorCheck(monitor);
        }
        return monitors;
    }

    private void runMonitorCheck(Monitor monitor) {
        switch(monitor.getType()){
            case "minecraft":
                MCInterfacer.pingServer((MinecraftMonitor) monitor);
                break;
            case "api":
                APIInterfacer.pingAPI((APIMonitor) monitor);
                break;
            case "web":
                WebInterfacer.pingWeb((WebMonitor) monitor);
                break;
        }
    }

    private LinkedList<Monitor> loadMonitors(){
        LinkedList<Monitor> monitors = new LinkedList<>();
        try {
            ObjectMapper om = new ObjectMapper();
            String jsonString = new String(Files.readAllBytes(Paths.get(PATH)));
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

    private void saveMonitorData(Monitor monitor){
        File dataDir = new File("data");
        if(!dataDir.exists()){
            dataDir.mkdir();
        }
        LinkedList<Monitor> history = loadHistoryData(monitor);
        history.add(monitor);
        history.sort(Comparator.comparing(Monitor::getTaken));
        if(history.size() > 100){
            history.removeFirst();
        }
        ObjectWriter writer = new ObjectMapper().writer(new DefaultPrettyPrinter());
        try {
            writer.writeValue(new File(dataDir.getPath() + "/" + monitor.getId() + ".json"), history);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private LinkedList<Monitor> loadHistoryData(int id){
        LinkedList<Monitor> monitors = loadMonitors();
        for(Monitor monitor: monitors){
            if(monitor.getId() == id){
                return loadHistoryData(monitor);
            }
        }
        return new LinkedList<>();
    }

    private LinkedList<Monitor> loadHistoryData(Monitor monitor){
        File dataDir = new File("data");
        File monitorFile = new File(dataDir.getPath() + "/" + monitor.getId() + ".json");
        ObjectMapper om = new ObjectMapper();
        try {
            Monitor[] monitors = (Monitor[]) om.readValue(monitorFile, classMap.get(monitor.getType() + "[]"));
            return new LinkedList<>(Arrays.asList(monitors));
        } catch (IOException e) {
            e.printStackTrace();
            return new LinkedList<>();
        }
    }

    private void saveNewMonitor(Monitor monitor){
        LinkedList<Monitor> monitors = loadMonitors();
        monitor.setId(monitors.size());
        monitors.add(monitor);
        ObjectMapper mapper = new ObjectMapper();
        mapper.addMixIn(MinecraftMonitor.class, com.zgamelogic.data.mixins.MinecraftMonitor.class);
        mapper.addMixIn(WebMonitor.class, com.zgamelogic.data.mixins.WebMonitor.class);
        mapper.addMixIn(APIMonitor.class, com.zgamelogic.data.mixins.ApiMonitor.class);
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        try {
            writer.writeValue(new File(PATH), monitors);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
