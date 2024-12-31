package com.zgamelogic.data.agentHistory;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zgamelogic.data.agents.Agent;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@NoArgsConstructor
public class AgentStatus {
    @Id
    private AgentStatusId id;
    private long memoryUsage;
    private long cpuUsage;
    private long diskUsage;
    private String agentVersion;

    public AgentStatus(AgentAPIStatus agentStatus){
        
    }

    public AgentStatus(long agentId, long memoryUsage, long cpuUsage, long diskUsage, String agentVersion) {
        this.memoryUsage = memoryUsage;
        this.cpuUsage = cpuUsage;
        this.diskUsage = diskUsage;
        this.agentVersion = agentVersion;
        this.id = new AgentStatusId(agentId, new Date());
    }

    @Embeddable
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class AgentStatusId {
        @ManyToOne(cascade = CascadeType.ALL)
        @JoinColumn(name = "AGENT_ID", referencedColumnName = "ID")
        private Agent agent;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private Date date;

        public AgentStatusId(long agentId, Date date) {
            agent = new Agent(agentId);
            this.date = date;
        }
    }
}
