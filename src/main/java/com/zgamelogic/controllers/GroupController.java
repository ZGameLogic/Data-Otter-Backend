package com.zgamelogic.controllers;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zgamelogic.data.groupConfiguration.MonitorGroup;
import com.zgamelogic.data.groupConfiguration.MonitorGroupRepository;
import com.zgamelogic.data.monitorConfiguration.MonitorConfiguration;
import com.zgamelogic.data.monitorConfiguration.MonitorConfigurationRepository;
import com.zgamelogic.serialization.GroupNoMonitorsSerialization;
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
    @JsonSerialize(using = GroupNoMonitorsSerialization.class)
    public ResponseEntity<MonitorGroup> createGroup(@RequestBody MonitorGroup monitorGroup){
        MonitorGroup group = monitorGroupRepository.save(monitorGroup);
        return ResponseEntity.ok(group);
    }

    @DeleteMapping("groups/{groupId}")
    public ResponseEntity<?> deleteGroup(@PathVariable Long groupId){
        Optional<MonitorGroup> group = monitorGroupRepository.findById(groupId);
        if(group.isEmpty()) return ResponseEntity.badRequest().build();
        monitorGroupRepository.delete(group.get());
        return ResponseEntity.ok().build();
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
}
