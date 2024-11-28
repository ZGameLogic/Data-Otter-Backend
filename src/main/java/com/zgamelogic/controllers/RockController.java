package com.zgamelogic.controllers;

import com.zgamelogic.data.components.DynamicApplicationRepository;
import com.zgamelogic.data.components.DynamicRockRepository;
import com.zgamelogic.data.entities.Rock;
import com.zgamelogic.data.repositories.RockRepository;
import com.zgamelogic.services.DataOtterWebsocketService;
import com.zgamelogic.data.exceptions.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

@RestController
@RequestMapping("rocks")
@PropertySource("File:./api.properties")
public class RockController {

    private final DataOtterWebsocketService websocketService;
    private final DynamicApplicationRepository applicationRepository;
    private final DynamicRockRepository rockRepository;
    private final String apiKey;

    public RockController(
            DataOtterWebsocketService websocketService,
            DynamicApplicationRepository applicationRepository,
            DynamicRockRepository rockRepository,
            @Value("${api-key}") String apiKey
    ) {
        this.websocketService = websocketService;
        this.applicationRepository = applicationRepository;
        this.rockRepository = rockRepository;
        this.apiKey = apiKey;
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

    @GetMapping("/{appId}")
    private ResponseEntity<Page<Rock>> getRock(
            @PathVariable long appId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if(!applicationRepository.existsById(appId)) return ResponseEntity.notFound().build();
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(rockRepository.findAllById_Application_IdOrderById_DateDesc(appId, pageable));
    }

    @ModelAttribute
    public void authenticate(WebRequest request, Model model) {
        String apiKey = request.getHeader("api-key");
        if (apiKey == null || !apiKey.equals(this.apiKey)) throw new UnauthorizedException();
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> handleUnauthorizedException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
