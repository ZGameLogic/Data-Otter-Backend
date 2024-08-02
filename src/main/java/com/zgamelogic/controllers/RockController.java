package com.zgamelogic.controllers;

import com.zgamelogic.data.application.ApplicationRepository;
import com.zgamelogic.data.rock.Rock;
import com.zgamelogic.data.rock.RockRepository;
import com.zgamelogic.services.DataOtterWebsocketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("rocks")
public class RockController {

    private final DataOtterWebsocketService websocketService;
    private final ApplicationRepository applicationRepository;
    private final RockRepository rockRepository;

    public RockController(DataOtterWebsocketService websocketService, ApplicationRepository applicationRepository, RockRepository rockRepository) {
        this.websocketService = websocketService;
        this.applicationRepository = applicationRepository;
        this.rockRepository = rockRepository;
    }

    @PostMapping("/{appId}")
    private ResponseEntity<?> createRock(
            @PathVariable long appId,
            @RequestBody String pebble
    ) {
        if(!applicationRepository.existsById(appId)) return ResponseEntity.notFound().build();
        Rock rock = new Rock(appId, pebble);
        Rock saved = rockRepository.save(rock);
        return ResponseEntity.ok(saved);
    }

}
