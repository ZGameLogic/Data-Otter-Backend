package com.zgamelogic.controllers;

import com.zgamelogic.data.groupConfiguration.MonitorGroup;
import com.zgamelogic.data.groupConfiguration.MonitorGroupRepository;
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
    private final MonitorGroupRepository monitorGroupRepository;
    private final MonitorService monitorService;

    public MonitorController(MonitorConfigurationRepository monitorConfigurationRepository, MonitorStatusRepository monitorStatusRepository, NodeMonitorReportRepository nodeMonitorReportRepository, MonitorGroupRepository monitorGroupRepository, MonitorService monitorService) {
        this.monitorConfigurationRepository = monitorConfigurationRepository;
        this.monitorStatusRepository = monitorStatusRepository;
        this.nodeMonitorReportRepository = nodeMonitorReportRepository;
        this.monitorGroupRepository = monitorGroupRepository;
        this.monitorService = monitorService;
    }

    @PostMapping("monitors")
    private ResponseEntity<?> createMonitor(@RequestBody MonitorConfiguration monitorConfiguration) throws ExecutionException, InterruptedException {
        MonitorStatusReport status = monitorService.getMonitorStatus(monitorConfiguration).get();
        if(!status.status()) return ResponseEntity.status(400).body(status);
        if(monitorConfiguration.getGroups() != null) {
            List<MonitorGroup> groups = monitorGroupRepository.findAllById(monitorConfiguration.getGroups().stream().map(MonitorGroup::getId).toList());
            groups.forEach(group -> group.getMonitors().add(monitorConfiguration));
            monitorConfiguration.setGroups(groups);
        }
        MonitorConfiguration m = monitorConfigurationRepository.save(monitorConfiguration);
        return ResponseEntity.ok(m);
    }

    @PostMapping("monitors/test")
    private ResponseEntity<MonitorStatusReport> createMonitorTest(@RequestBody MonitorConfiguration monitorConfiguration) throws ExecutionException, InterruptedException {
        MonitorStatusReport status = monitorService.getMonitorStatus(monitorConfiguration).get();
        if(!status.status()) return ResponseEntity.status(400).body(status);
        return ResponseEntity.ok(status);
    }

    @GetMapping("monitors")
    private ResponseEntity<List<MonitorConfigurationAndStatus>> getAllMonitors(
            @RequestParam(required = false, name = "include-status") Boolean includeStatus
    ) {
        List<MonitorConfigurationAndStatus> payload = new ArrayList<>();
        monitorConfigurationRepository.findAll().forEach(monitorConfiguration -> {
            if(includeStatus != null && includeStatus) {
                Optional<MonitorStatus> mostRecentStatus = monitorStatusRepository.findTop1ById_MonitorIdOrderById_DateDesc(monitorConfiguration.getId());
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
        List<MonitorConfigurationAndStatus> sortedPayload = payload.stream().sorted(Comparator.comparing(c -> c.monitorConfiguration().getId())).toList();
        return ResponseEntity.ok(sortedPayload);
    }

    @GetMapping("monitors/{id}")
    private ResponseEntity<MonitorConfigurationAndStatus> getMonitorStatus(
            @PathVariable long id,
            @RequestParam(required = false, name = "include-status") Boolean includeStatus
    ) {
        Optional<MonitorConfiguration> oMonitor = monitorConfigurationRepository.findById(id);
        if(oMonitor.isEmpty()) return ResponseEntity.notFound().build();
        MonitorConfiguration monitor = oMonitor.get();
        if(includeStatus != null && includeStatus) {
            Optional<MonitorStatus> mostRecentStatus = monitorStatusRepository.findTop1ById_MonitorIdOrderById_DateDesc(id);
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

    @DeleteMapping("monitors/{id}")
    private ResponseEntity<?> deleteMonitor(@PathVariable long id) {
        if(!monitorConfigurationRepository.existsById(id)) return ResponseEntity.notFound().build();
        monitorStatusRepository.deleteAllById_MonitorId(id);
        nodeMonitorReportRepository.deleteAllById_MonitorId(id);
        monitorConfigurationRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("monitors/{id}")
    private ResponseEntity<MonitorConfiguration> updateMonitor(
            @PathVariable long id,
            @RequestBody MonitorConfiguration updatedConfiguration
    ) {
        if(!monitorConfigurationRepository.existsById(id)) return ResponseEntity.notFound().build();
        MonitorConfiguration original  = monitorConfigurationRepository.findById(id).get();
        original.update(updatedConfiguration);
        if(updatedConfiguration.getGroups() != null) {
            List<MonitorGroup> groups = monitorGroupRepository.findAllById(updatedConfiguration.getGroups().stream().map(MonitorGroup::getId).toList());
            groups.forEach(group -> group.getMonitors().add(updatedConfiguration));
            updatedConfiguration.setGroups(groups);
        }
        MonitorConfiguration saved = monitorConfigurationRepository.save(original);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("monitors/{id}/history")
    private ResponseEntity<List<MonitorStatus>> getMonitorHistory(
            @PathVariable long id,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "MM-dd-yyyy HH:mm:ss")
            Date start,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "MM-dd-yyyy HH:mm:ss")
            Date end,
            @RequestParam(required = false)
            Boolean condensed
    ) {
        if(!monitorConfigurationRepository.existsById(id)) return ResponseEntity.notFound().build();
        if (end == null) end = new Date();
        if (start == null) start = Date.from(end.toInstant().minus(7, ChronoUnit.DAYS));
        List<MonitorStatus> history = monitorStatusRepository.findByMonitorIdAndDateBetween(id, start, end);
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

    @PostMapping("monitors/{monitorId}/group/{groupId}")
    public ResponseEntity<MonitorGroup> addToGroup(
            @PathVariable Long monitorId,
            @PathVariable Long groupId
    ){
        Optional<MonitorGroup> group = monitorGroupRepository.findById(groupId);
        if(group.isEmpty()) return ResponseEntity.badRequest().build();
        Optional<MonitorConfiguration> configuration = monitorConfigurationRepository.findById(monitorId);
        if(configuration.isEmpty()) return ResponseEntity.badRequest().build();
        group.get().getMonitors().add(configuration.get());
        MonitorGroup savedGroup = monitorGroupRepository.save(group.get());
        return ResponseEntity.ok(savedGroup);
    }


    @DeleteMapping("monitors/{monitorId}/group/{groupId}")
    public ResponseEntity<MonitorGroup> removeFromGroup(
            @PathVariable Long monitorId,
            @PathVariable Long groupId
    ){
        Optional<MonitorGroup> group = monitorGroupRepository.findById(groupId);
        if(group.isEmpty()) return ResponseEntity.badRequest().build();
        Optional<MonitorConfiguration> configuration = monitorConfigurationRepository.findById(monitorId);
        if(configuration.isEmpty()) return ResponseEntity.badRequest().build();
        group.get().getMonitors().remove(configuration.get());
        MonitorGroup savedGroup = monitorGroupRepository.save(group.get());
        return ResponseEntity.ok(savedGroup);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    private ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
