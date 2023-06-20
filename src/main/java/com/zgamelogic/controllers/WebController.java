package com.zgamelogic.controllers;

import com.zgamelogic.data.APIMonitor;
import com.zgamelogic.data.MinecraftMonitor;
import com.zgamelogic.data.Monitor;
import com.zgamelogic.data.WebMonitor;
import com.zgamelogic.helpers.APIInterfacer;
import com.zgamelogic.helpers.MCInterfacer;
import com.zgamelogic.helpers.WebInterfacer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.LinkedList;

@RestController
@Slf4j
public class WebController {

    @PostConstruct
    private void init(){

    }

    @GetMapping("monitors")
    private LinkedList<Monitor> getMonitors(){
        return getMonitorStatus();
    }

    private LinkedList<Monitor> getMonitorStatus(){
        LinkedList<Monitor> monitors = new LinkedList<>();

        MinecraftMonitor minecraft = new MinecraftMonitor("zgamelogic.com", 25565, "ATM8 Minecraft Server");
        MCInterfacer.pingServer(minecraft);
        monitors.add(minecraft);

        APIMonitor wraith = new APIMonitor("Wraith API", "https://zgamelogic.com", 2002, "health");
        APIInterfacer.pingAPI(wraith);
        monitors.add(wraith);

        APIMonitor discord = new APIMonitor("Discord API", "https://zgamelogic.com", 2000, "health");
        APIInterfacer.pingAPI(discord);
        monitors.add(discord);

        APIMonitor websiteApi = new APIMonitor("Discord API", "https://zgamelogic.com", 443, "api/health");
        APIInterfacer.pingAPI(websiteApi);
        monitors.add(websiteApi);

        WebMonitor jira = new WebMonitor("Jira", "https://zgamelogic.com", 8080, "System Dashboard");
        WebInterfacer.pingWeb(jira);
        monitors.add(jira);

        WebMonitor bitbucket = new WebMonitor("Bitbucket", "https://zgamelogic.com", 7990, "Introduction to collections");
        WebInterfacer.pingWeb(bitbucket);
        monitors.add(bitbucket);

        WebMonitor bamboo = new WebMonitor("Bamboo", "https://zgamelogic.com", 8085, "Build dashboard");
        WebInterfacer.pingWeb(bamboo);
        monitors.add(bamboo);

        return monitors;
    }
}
