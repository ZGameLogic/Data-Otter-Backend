package com.zgamelogic.controllers;

import com.zgamelogic.data.agentHistory.AgentReport;
import com.zgamelogic.data.agentHistory.AgentStatus;
import com.zgamelogic.data.agentHistory.AgentStatusRepository;
import com.zgamelogic.data.agents.AgentWithLastStatus;
import com.zgamelogic.data.agents.Agent;
import com.zgamelogic.data.agents.AgentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.zgamelogic.data.Constants.AGENT_STATUS_MISSING_MINUTE_COUNT;

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
    public ResponseEntity<AgentStatus> status(@RequestBody AgentReport status, @PathVariable long agentId) {
        AgentStatus saved = agentStatusRepository.save(new AgentStatus(agentId, status));
        return ResponseEntity.ok(saved);
    }

    @GetMapping("agent/{agentId}")
    public ResponseEntity<Agent> getAgent(@PathVariable long agentId) {
        if(!agentRepository.existsById(agentId)) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(agentRepository.findById(agentId).get());
    }

    @GetMapping("agents")
    public ResponseEntity<List<Agent>> getAgents() {
        return ResponseEntity.ok(agentRepository.findAll());
    }

    @DeleteMapping("agent/{agentId}")
    public ResponseEntity<?> removeAgent(@PathVariable long agentId) {
        if(!agentRepository.existsById(agentId)) return ResponseEntity.notFound().build();
        agentRepository.deleteById(agentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("agent/{agentId}/status")
    public ResponseEntity<AgentWithLastStatus> getAgentStatus(@PathVariable long agentId) {
        if(!agentRepository.existsById(agentId)) return ResponseEntity.notFound().build();
        Agent agent = agentRepository.findById(agentId).get();
        Optional<AgentStatus> status = agentStatusRepository.findFirstByIdAgentIdAndIdDateAfterOrderByIdDateDesc(agentId, Date.from(Instant.now().minus(AGENT_STATUS_MISSING_MINUTE_COUNT, ChronoUnit.MINUTES)));
        return status.map(
                agentStatus -> ResponseEntity.ok(new AgentWithLastStatus(agentStatus, agent))
                ).orElseGet(
                        () -> ResponseEntity.ok(new AgentWithLastStatus(null, agent)
        ));
    }
}
