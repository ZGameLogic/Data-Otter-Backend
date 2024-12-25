package com.zgamelogic.controllers;

import com.zgamelogic.data.application.ApplicationRepository;
import com.zgamelogic.data.rock.Rock;
import com.zgamelogic.data.rock.RockRepository;
import com.zgamelogic.services.DataOtterWebsocketService;
import com.zgamelogic.data.exceptions.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("rocks")
@PropertySource("File:./api.properties")
@Slf4j
public class RockController {

    private final DataOtterWebsocketService websocketService;
    private final ApplicationRepository applicationRepository;
    private final RockRepository rockRepository;
    private final String apiKey;

    public RockController(
            DataOtterWebsocketService websocketService,
            ApplicationRepository applicationRepository,
            RockRepository rockRepository,
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

    @GetMapping("/stats")
    private ResponseEntity<?> stats() {
        List<Long> applicationIds = applicationRepository.findAllIds();
        Map<Long, Long> result = applicationIds.stream()
        .collect(Collectors.toMap(
                id -> id,
                rockRepository::countAllById_Application_Id
        ));
        return ResponseEntity.ok(result);
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
