package com.zgamelogic.controllers;

import com.zgamelogic.data.agentHistory.AgentAPIStatus;
import com.zgamelogic.data.agentHistory.AgentStatus;
import com.zgamelogic.data.agentHistory.AgentStatusRepository;
import com.zgamelogic.data.agents.Agent;
import com.zgamelogic.data.agents.AgentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
        AgentStatus saved = agentStatusRepository.save(status);
        return ResponseEntity.ok(saved);
    }
}
