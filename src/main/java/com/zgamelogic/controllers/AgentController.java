package com.zgamelogic.controllers;

import com.zgamelogic.data.agents.Agent;
import com.zgamelogic.data.agents.AgentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AgentController {
    private final AgentRepository agentRepository;

    public AgentController(AgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }

    @PostMapping("register")
    public ResponseEntity<Agent> register(@RequestBody Agent agent) {

    }
}
