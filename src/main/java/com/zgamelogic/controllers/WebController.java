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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;

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
    }

    @GetMapping("monitors")
    private LinkedList<Monitor> getMonitors(){
        return getMonitorStatus();
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
            switch(m.getType()){
                case "minecraft":
                    MCInterfacer.pingServer((MinecraftMonitor) m);
                    break;
                case "api":
                    APIInterfacer.pingAPI((APIMonitor) m);
                    break;
                case "web":
                    WebInterfacer.pingWeb((WebMonitor) m);
                    break;
            }
            return m;
        } catch (IOException e) {
            return null;
        }
    }

    private LinkedList<Monitor> getMonitorStatus(){
        LinkedList<Monitor> monitors = loadMonitors();

        for(Monitor monitor: monitors){
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
        return monitors;
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
