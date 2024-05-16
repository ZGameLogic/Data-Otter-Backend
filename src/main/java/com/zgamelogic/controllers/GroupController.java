package com.zgamelogic.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.zgamelogic.data.groupConfiguration.MonitorGroup;
import com.zgamelogic.data.groupConfiguration.MonitorGroupRepository;
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

    public GroupController(MonitorGroupRepository monitorGroupRepository) {
        this.monitorGroupRepository = monitorGroupRepository;
    }

    @GetMapping("groups")
    public ResponseEntity<List<MonitorGroup>> getGroups(){
        return ResponseEntity.ok(monitorGroupRepository.findAll());
    }

    @PostMapping("groups")
    @JsonSerialize(using = GroupNoMonitorsSerialization.class)
    public ResponseEntity<String> createGroup(@RequestBody MonitorGroup monitorGroup) throws JsonProcessingException {
        MonitorGroup group = monitorGroupRepository.save(monitorGroup);
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new SimpleModule().addSerializer(MonitorGroup.class, new GroupNoMonitorsSerialization()));
        return ResponseEntity.ok(om.writeValueAsString(group));
    }

    @DeleteMapping("groups/{groupId}")
    public ResponseEntity<?> deleteGroup(@PathVariable Long groupId){
        Optional<MonitorGroup> group = monitorGroupRepository.findById(groupId);
        if(group.isEmpty()) return ResponseEntity.badRequest().build();
        monitorGroupRepository.delete(group.get());
        return ResponseEntity.ok().build();
    }
}
