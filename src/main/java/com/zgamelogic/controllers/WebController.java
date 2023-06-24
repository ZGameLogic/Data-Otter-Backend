package com.zgamelogic.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;

@RestController
@Slf4j
public class WebController {
    private HashMap<String, Class> classMap;

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
            String jsonString = new String(Files.readAllBytes(Paths.get("monitors.json")));
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
}
