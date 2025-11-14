package com.zgamelogic.dataotter.agent.database;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AgentReport {
    private long memoryUsage;
    private long cpuUsage;
    private long diskUsage;
    private String agentVersion;
}
