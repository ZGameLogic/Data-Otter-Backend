package com.zgamelogic.data.agentHistory;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zgamelogic.data.agents.Agent;
import jakarta.persistence.*;
import lombok.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@NoArgsConstructor
@Getter
@JsonSerialize(using = AgentStatus.AgentStatusSerializer.class)
public class AgentStatus {
    @Id
    private AgentStatusId id;
    private long memoryUsage;
    private long cpuUsage;
    private long diskUsage;
    private String agentVersion;

    public AgentStatus(long agentId, AgentReport agentStatus){
        this(
            agentId,
            agentStatus.getMemoryUsage(),
            agentStatus.getCpuUsage(),
            agentStatus.getDiskUsage(),
            agentStatus.getAgentVersion()
        );
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

    public static class AgentStatusSerializer extends JsonSerializer<AgentStatus> {
        @Override
        public void serialize(AgentStatus value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeStartObject();
            gen.writeNumberField("agent id", value.getId().agent.getId());
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value.getId().getDate());
            gen.writeStringField("date", date);
            gen.writeNumberField("memory usage", value.getMemoryUsage());
            gen.writeNumberField("cpu usage", value.getCpuUsage());
            gen.writeNumberField("disk usage", value.getDiskUsage());
            gen.writeStringField("agent version", value.getAgentVersion());
            gen.writeEndObject();
        }
    }
}
