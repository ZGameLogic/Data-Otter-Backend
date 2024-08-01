package com.zgamelogic.controllers;

import com.zgamelogic.services.DataOtterWebsocketService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("rock")
public class RockController {

    private final DataOtterWebsocketService websocketService;

    public RockController(DataOtterWebsocketService websocketService) {
        this.websocketService = websocketService;
    }
}
