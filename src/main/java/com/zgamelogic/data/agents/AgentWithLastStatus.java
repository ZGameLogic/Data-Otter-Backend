package com.zgamelogic.data.agents;

import com.zgamelogic.data.agentHistory.AgentStatus;

public record AgentWithLastStatus(AgentStatus status, Agent agent) {
}
