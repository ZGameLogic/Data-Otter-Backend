package com.zgamelogic.controllers;

import com.zgamelogic.data.application.ApplicationRepository;
import com.zgamelogic.data.rock.Rock;
import com.zgamelogic.data.rock.RockRepository;
import com.zgamelogic.services.DataOtterWebsocketService;
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

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> handleUnauthorizedException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @ModelAttribute
    public void authenticate(WebRequest request, Model model) {
        String apiKey = request.getHeader("api-key");
        // TODO actual check
//        if (token == null || device == null) throw new UnauthorizedException();

    }

    private static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException() {
            super("Unauthorized");
        }
    }
}
