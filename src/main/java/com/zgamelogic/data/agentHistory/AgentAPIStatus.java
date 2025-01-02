package com.zgamelogic.data.agentHistory;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AgentAPIStatus {
    private long memoryUsage;
    private long cpuUsage;
    private long diskUsage;
    private String agentVersion;
}
