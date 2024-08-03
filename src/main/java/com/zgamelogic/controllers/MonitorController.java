package com.zgamelogic.controllers;

import com.zgamelogic.App;
import com.zgamelogic.data.application.Application;
import com.zgamelogic.data.application.ApplicationRepository;
import com.zgamelogic.data.monitorConfiguration.MonitorConfiguration;
import com.zgamelogic.data.monitorConfiguration.MonitorConfigurationAndStatus;
import com.zgamelogic.data.monitorConfiguration.MonitorConfigurationRepository;
import com.zgamelogic.data.monitorHistory.MonitorStatus;
import com.zgamelogic.data.monitorHistory.MonitorStatusRepository;
import com.zgamelogic.data.nodeMonitorReport.NodeMonitorReportRepository;
import com.zgamelogic.services.monitors.MonitorService;
import com.zgamelogic.services.monitors.MonitorStatusReport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
@RestController
public class MonitorController {
    private final MonitorConfigurationRepository monitorConfigurationRepository;
    private final MonitorStatusRepository monitorStatusRepository;
    private final NodeMonitorReportRepository nodeMonitorReportRepository;
    private final MonitorService monitorService;
    private final ApplicationRepository applicationRepository;

    public MonitorController(MonitorConfigurationRepository monitorConfigurationRepository, MonitorStatusRepository monitorStatusRepository, NodeMonitorReportRepository nodeMonitorReportRepository, MonitorService monitorService, App app, ApplicationRepository applicationRepository, ApplicationRepository applicationRepository1) {
        this.monitorConfigurationRepository = monitorConfigurationRepository;
        this.monitorStatusRepository = monitorStatusRepository;
        this.nodeMonitorReportRepository = nodeMonitorReportRepository;
        this.monitorService = monitorService;
        this.applicationRepository = applicationRepository1;
    }

    @PostMapping("monitors/{appId}")
    private ResponseEntity<?> createMonitor(
            @PathVariable long appId,
            @RequestBody MonitorConfiguration monitorConfiguration
    ) throws ExecutionException, InterruptedException {
        if(!applicationRepository.existsById(appId)) return ResponseEntity.notFound().build();
        MonitorStatusReport status = monitorService.getMonitorStatus(monitorConfiguration).get();
        if(!status.status()) return ResponseEntity.status(400).body(status);
        monitorConfiguration.getId().setApplication(new Application(appId));
        MonitorConfiguration m = monitorConfigurationRepository.save(monitorConfiguration);
        return ResponseEntity.ok(monitorConfigurationRepository.findById_MonitorConfigurationIdAndId_Application_Id(m.getId().getMonitorConfigurationId(), appId).get());
    }

    @PostMapping("monitors/test")
    private ResponseEntity<MonitorStatusReport> createMonitorTest(@RequestBody MonitorConfiguration monitorConfiguration) throws ExecutionException, InterruptedException {
        MonitorStatusReport status = monitorService.getMonitorStatus(monitorConfiguration).get();
        if(!status.status()) return ResponseEntity.status(400).body(status);
        return ResponseEntity.ok(status);
    }

    @GetMapping("monitors/{appId}")
    private ResponseEntity<List<MonitorConfigurationAndStatus>> getAllMonitors(
            @PathVariable long appId,
            @RequestParam(required = false, name = "include-status") Boolean includeStatus,
            @RequestParam(required = false, name = "active") Boolean activeOnly
    ) {
        List<MonitorConfigurationAndStatus> payload = new ArrayList<>();
        List<MonitorConfiguration> configurations = activeOnly != null && activeOnly ? monitorConfigurationRepository.findAllByActiveIsTrue() : monitorConfigurationRepository.findAll();
        configurations.forEach(monitorConfiguration -> {
            if(includeStatus != null && includeStatus) {
                Optional<MonitorStatus> mostRecentStatus = monitorStatusRepository.findTopById_Monitor_Id_MonitorConfigurationIdAndId_Monitor_Id_Application_IdOrderById_Date(monitorConfiguration.getId().getMonitorConfigurationId(), appId);
                payload.add(new MonitorConfigurationAndStatus(
                        monitorConfiguration,
                        mostRecentStatus.orElse(null)
                ));
            } else {
                payload.add(new MonitorConfigurationAndStatus(
                        monitorConfiguration,
                        null
                ));
            }
        });
        List<MonitorConfigurationAndStatus> sortedPayload = payload.stream().sorted(Comparator.comparing(c -> c.monitorConfiguration().getId().getMonitorConfigurationId())).toList();
        return ResponseEntity.ok(sortedPayload);
    }

    @GetMapping("monitors/{appId}/{id}")
    private ResponseEntity<MonitorConfigurationAndStatus> getMonitorStatus(
            @PathVariable long id,
            @PathVariable long appId,
            @RequestParam(required = false, name = "include-status") Boolean includeStatus
    ) {
        Optional<MonitorConfiguration> oMonitor = monitorConfigurationRepository.findById_MonitorConfigurationIdAndId_Application_Id(id, appId);
        if(oMonitor.isEmpty()) return ResponseEntity.notFound().build();
        MonitorConfiguration monitor = oMonitor.get();
        if(includeStatus != null && includeStatus) {
            Optional<MonitorStatus> mostRecentStatus = monitorStatusRepository.findTopById_Monitor_Id_MonitorConfigurationIdAndId_Monitor_Id_Application_IdOrderById_Date(id, appId);
            MonitorConfigurationAndStatus payload = new MonitorConfigurationAndStatus(
                    monitor,
                    mostRecentStatus.orElse(null)
            );
            return ResponseEntity.ok(payload);
        } else {
            return ResponseEntity.ok(new MonitorConfigurationAndStatus(
                    monitor,
                    null
            ));
        }
    }

    @DeleteMapping("monitors/{appId}/{id}")
    private ResponseEntity<?> deleteMonitor(@PathVariable long appId, @PathVariable long id) {
        if(!monitorConfigurationRepository.existsById_MonitorConfigurationIdAndId_Application_Id(id, appId)) return ResponseEntity.notFound().build();
        monitorStatusRepository.deleteAllById_monitor_id_monitorConfigurationIdAndId_Monitor_Id_Application_Id(id, appId);
        nodeMonitorReportRepository.deleteAllById_monitor_id_monitorConfigurationId(id);
        monitorConfigurationRepository.deleteById_MonitorConfigurationId(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("monitors/{appId}/{id}")
    private ResponseEntity<MonitorConfiguration> updateMonitor(
            @PathVariable long id,
            @PathVariable long appId,
            @RequestBody MonitorConfiguration updatedConfiguration
    ) {
        if(!monitorConfigurationRepository.existsById_MonitorConfigurationIdAndId_Application_Id(id, appId)) return ResponseEntity.notFound().build();
        MonitorConfiguration original  = monitorConfigurationRepository.findById_MonitorConfigurationIdAndId_Application_Id(id, appId).get();
        original.update(updatedConfiguration);
        MonitorConfiguration saved = monitorConfigurationRepository.save(original);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("monitors/{appId}/{id}/history")
    private ResponseEntity<List<MonitorStatus>> getMonitorHistory(
            @PathVariable long id,
            @PathVariable long appId,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "MM-dd-yyyy HH:mm:ss")
            Date start,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "MM-dd-yyyy HH:mm:ss")
            Date end,
            @RequestParam(required = false)
            Boolean condensed
    ) {
        if(!monitorConfigurationRepository.existsById_MonitorConfigurationIdAndId_Application_Id(id, appId)) return ResponseEntity.notFound().build();
        if (end == null) end = new Date();
        if (start == null) start = Date.from(end.toInstant().minus(7, ChronoUnit.DAYS));
        List<MonitorStatus> history = monitorStatusRepository.findByMonitorIdAndDateBetween(id, start, end, appId);
        if(condensed != null && condensed && !history.isEmpty()) {
            List<Integer> changeIndices = IntStream.range(0, history.size())
                    .filter(
                            i -> (i == 0 || i == history.size() - 1 ||
                                    history.get(i).isStatus() != history.get(i - 1).isStatus() ||
                                    history.get(i).isStatus() != history.get(i + 1).isStatus())
                    )
                    .boxed()
                    .toList();
            List<MonitorStatus> condensedList = changeIndices.stream()
                    .flatMap(idx -> Stream.of(history.get(idx)))
                    .distinct()
                    .toList();
            return ResponseEntity.ok(condensedList);
        }
        return ResponseEntity.ok(history);
    }

    @PostMapping("monitors/{appId}/{monitorId}/active/{active}")
    public ResponseEntity<?> disableMonitor(@PathVariable long monitorId, @PathVariable long appId, @PathVariable boolean active){
        Optional<MonitorConfiguration> configuration = monitorConfigurationRepository.findById_MonitorConfigurationIdAndId_Application_Id(monitorId, appId);
        if(configuration.isEmpty()) return ResponseEntity.badRequest().build();
        configuration.get().setActive(active);
        monitorConfigurationRepository.save(configuration.get());
        nodeMonitorReportRepository.deleteAllById_monitor_id_monitorConfigurationId(monitorId);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    private ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
