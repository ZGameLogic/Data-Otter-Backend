package com.zgamelogic.controllers;

import com.zgamelogic.data.application.ApplicationRepository;
import com.zgamelogic.services.DataOtterWebsocketService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("rock")
public class RockController {

    private final DataOtterWebsocketService websocketService;
    private final ApplicationRepository applicationRepository;

    public RockController(DataOtterWebsocketService websocketService, ApplicationRepository applicationRepository) {
        this.websocketService = websocketService;
        this.applicationRepository = applicationRepository;
    }

    @Scheduled(cron = "* * * * * *")
    private void second(){
        websocketService.sendMessage(applicationRepository.findAll());
    }
}
