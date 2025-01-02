package com.zgamelogic.controllers;

import com.zgamelogic.data.agentHistory.AgentAPIStatus;
import com.zgamelogic.data.agentHistory.AgentStatus;
import com.zgamelogic.data.agentHistory.AgentStatusRepository;
import com.zgamelogic.data.agents.Agent;
import com.zgamelogic.data.agents.AgentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AgentController {
    private final AgentRepository agentRepository;
    private final AgentStatusRepository agentStatusRepository;

    public AgentController(AgentRepository agentRepository, AgentStatusRepository agentStatusRepository) {
        this.agentRepository = agentRepository;
        this.agentStatusRepository = agentStatusRepository;
    }

    @PostMapping("agent/register")
    public ResponseEntity<Agent> register(@RequestBody Agent agent) {
        Agent saved  = agentRepository.save(agent);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("agent/{agentId}/status")
    public ResponseEntity<AgentStatus> status(@RequestBody AgentAPIStatus status, @PathVariable long agentId) {
        AgentStatus saved = agentStatusRepository.save(new AgentStatus(agentId, status));
        return ResponseEntity.ok(saved);
    }

    @GetMapping("agent/{agentId}")
    public ResponseEntity<Agent> getStatus(@PathVariable long agentId) {
        if(!agentRepository.existsById(agentId)) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(agentRepository.findById(agentId).get());
    }
}
