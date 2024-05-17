package com.zgamelogic.controllers;

import com.zgamelogic.data.groupConfiguration.MonitorGroup;
import com.zgamelogic.data.groupConfiguration.MonitorGroupRepository;
import com.zgamelogic.data.monitorConfiguration.MonitorConfiguration;
import com.zgamelogic.data.monitorConfiguration.MonitorConfigurationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class GroupController {
    private final MonitorGroupRepository monitorGroupRepository;
    private final MonitorConfigurationRepository monitorConfigurationRepository;

    public GroupController(MonitorGroupRepository monitorGroupRepository, MonitorConfigurationRepository monitorConfigurationRepository) {
        this.monitorGroupRepository = monitorGroupRepository;
        this.monitorConfigurationRepository = monitorConfigurationRepository;
    }

    @GetMapping("groups")
    public ResponseEntity<List<MonitorGroup>> getGroups(){
        return ResponseEntity.ok(monitorGroupRepository.findAll());
    }

    @PostMapping("groups")
    public ResponseEntity<MonitorGroup> createGroup(@RequestBody MonitorGroup monitorGroup) {
        MonitorGroup group = monitorGroupRepository.save(monitorGroup);
        if(monitorGroup.getMonitors() != null) {
            List<MonitorConfiguration> monitors = monitorConfigurationRepository.findAllById(monitorGroup.getMonitors().stream().map(MonitorConfiguration::getId).toList());
            monitors.forEach(monitor -> monitor.getGroups().add(group));
            group.setMonitors(monitors);
        }
        return ResponseEntity.ok(group);
    }

    @DeleteMapping("groups/{groupId}")
    public ResponseEntity<?> deleteGroup(@PathVariable Long groupId){
        Optional<MonitorGroup> group = monitorGroupRepository.findById(groupId);
        if(group.isEmpty()) return ResponseEntity.badRequest().build();
        monitorGroupRepository.delete(group.get());
        return ResponseEntity.ok().build();
    }
}
