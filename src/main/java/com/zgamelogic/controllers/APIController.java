package com.zgamelogic.controllers;

import com.zgamelogic.data.monitorConfiguration.MonitorConfiguration;
import com.zgamelogic.data.monitorConfiguration.MonitorConfigurationRepository;
import com.zgamelogic.data.monitorHistory.MonitorStatus;
import com.zgamelogic.data.monitorHistory.MonitorStatusRepository;
import com.zgamelogic.services.MonitorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class APIController {
    private final MonitorConfigurationRepository monitorConfigurationRepository;
    private final MonitorStatusRepository monitorStatusRepository;
    private final MonitorService monitorService;

    public APIController(MonitorConfigurationRepository monitorConfigurationRepository, MonitorStatusRepository monitorStatusRepository, MonitorService monitorService) {
        this.monitorConfigurationRepository = monitorConfigurationRepository;
        this.monitorStatusRepository = monitorStatusRepository;
        this.monitorService = monitorService;
    }

    @PostMapping("monitor")
    private ResponseEntity<?> createMonitor(@RequestBody MonitorConfiguration monitorConfiguration) {
        MonitorStatus status = monitorService.getMonitorStatus(monitorConfiguration);
        if(!status.isStatus()) return ResponseEntity.status(400).body(status);
        MonitorConfiguration m = monitorConfigurationRepository.save(monitorConfiguration);
        return ResponseEntity.ok(m);
    }

    @PostMapping("monitor/test")
    private ResponseEntity<MonitorStatus> createMonitorTest(@RequestBody MonitorConfiguration monitorConfiguration) {
        MonitorStatus status = monitorService.getMonitorStatus(monitorConfiguration);
        if(!status.isStatus()) return ResponseEntity.status(400).body(status);
        return ResponseEntity.ok(status);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    private ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
