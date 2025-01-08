package com.zgamelogic.controllers;

import com.zgamelogic.data.agentHistory.AgentReport;
import com.zgamelogic.data.agentHistory.AgentStatus;
import com.zgamelogic.data.agentHistory.AgentStatusRepository;
import com.zgamelogic.data.agents.AgentWithLastStatus;
import com.zgamelogic.data.agents.Agent;
import com.zgamelogic.data.agents.AgentRepository;
import org.springframework.format.annotation.DateTimeFormat;
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

    @GetMapping("agent/{agentId}/status/history")
    public ResponseEntity<List<AgentStatus>> statusHistory(
            @PathVariable long agentId,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "MM-dd-yyyy HH:mm:ss")
            Date start,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "MM-dd-yyyy HH:mm:ss")
            Date end
    ){
        if(!agentRepository.existsById(agentId)) return ResponseEntity.notFound().build();
        if (end == null) end = new Date();
        if (start == null) start = Date.from(end.toInstant().minus(7, ChronoUnit.DAYS));
        return ResponseEntity.ok(agentStatusRepository.findByAgentIdAndDateBetween(agentId, start, end));
    }

    @GetMapping("agents")
    public ResponseEntity<List<AgentWithLastStatus>> getAgents(@RequestParam(required = false, name = "include-status") Boolean includeStatus) {
        if(includeStatus == null || !includeStatus) return ResponseEntity.ok(agentRepository.findAll().stream().map(agent -> new AgentWithLastStatus(null, agent)).toList());
        return ResponseEntity.ok(
                agentRepository.findAll().stream().map(agent -> {
                    Optional<AgentStatus> status = agentStatusRepository.findFirstByIdAgentIdAndIdDateAfterOrderByIdDateDesc(agent.getId(), Date.from(Instant.now().minus(AGENT_STATUS_MISSING_MINUTE_COUNT, ChronoUnit.MINUTES)));
                    return status.map(
                            agentStatus -> new AgentWithLastStatus(agentStatus, agent)
                    ).orElseGet(
                            () -> new AgentWithLastStatus(null, agent)
                    );
                }).toList()
        );
    }

    @DeleteMapping("agent/{agentId}")
    public ResponseEntity<?> removeAgent(@PathVariable long agentId) {
        if(!agentRepository.existsById(agentId)) return ResponseEntity.notFound().build();
        agentRepository.deleteById(agentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("agent/{agentId}")
    public ResponseEntity<AgentWithLastStatus> getAgentStatus(
            @PathVariable long agentId,
            @RequestParam(required = false, name = "include-status") Boolean includeStatus
    ) {
        if(!agentRepository.existsById(agentId)) return ResponseEntity.notFound().build();
        Agent agent = agentRepository.findById(agentId).get();
        if(!includeStatus) return ResponseEntity.ok(new AgentWithLastStatus(null, agent));
        Optional<AgentStatus> status = agentStatusRepository.findFirstByIdAgentIdAndIdDateAfterOrderByIdDateDesc(agentId, Date.from(Instant.now().minus(AGENT_STATUS_MISSING_MINUTE_COUNT, ChronoUnit.MINUTES)));
        return status.map(
                agentStatus -> ResponseEntity.ok(new AgentWithLastStatus(agentStatus, agent))
                ).orElseGet(
                        () -> ResponseEntity.ok(new AgentWithLastStatus(null, agent))
        );
    }
}
