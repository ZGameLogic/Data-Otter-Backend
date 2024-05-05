package com.zgamelogic.controllers;

import com.zgamelogic.data.monitorConfiguration.MonitorConfiguration;
import com.zgamelogic.data.monitorConfiguration.MonitorConfigurationAndStatus;
import com.zgamelogic.data.monitorConfiguration.MonitorConfigurationRepository;
import com.zgamelogic.data.monitorHistory.MonitorStatus;
import com.zgamelogic.data.monitorHistory.MonitorStatusRepository;
import com.zgamelogic.services.MonitorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    @GetMapping("monitor/{id}")
    private ResponseEntity<MonitorConfigurationAndStatus> getMonitorStatus(@PathVariable long id) {
        Optional<MonitorConfiguration> oMonitor = monitorConfigurationRepository.findById(id);
        if(oMonitor.isEmpty()) return ResponseEntity.notFound().build();
        MonitorConfiguration monitor = oMonitor.get();
        Optional<MonitorStatus> mostRecentStatus = monitorStatusRepository.findTop1ById_MonitorIdOrderById_DateDesc(id);
        MonitorConfigurationAndStatus payload = new MonitorConfigurationAndStatus(
                monitor,
                mostRecentStatus.orElse(null)
        );
        return ResponseEntity.ok(payload);
    }

    @GetMapping("monitor/{id}/history")
    private ResponseEntity<List<MonitorStatus>> getMonitorHistory(
            @PathVariable long id,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-dd-MM HH:mm:ss")
            Date start,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-dd-MM HH:mm:ss")
            Date end
    ) {
        if(!monitorConfigurationRepository.existsById(id)) return ResponseEntity.notFound().build();
        if (end == null) end = new Date();
        if (start == null) start = Date.from(end.toInstant().minus(7, ChronoUnit.DAYS));
        List<MonitorStatus> history = monitorStatusRepository.findByMonitorIdAndDateBetween(id, start, end);
        return ResponseEntity.ok(history);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    private ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
