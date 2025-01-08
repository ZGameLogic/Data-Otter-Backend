package com.zgamelogic.data.agents;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zgamelogic.data.agentHistory.AgentStatus;

import java.io.IOException;
import java.text.SimpleDateFormat;

@JsonSerialize(using = AgentWithLastStatus.AgentWithLastStatusSerializer.class)
public record AgentWithLastStatus(AgentStatus status, Agent agent) {

    public static class AgentWithLastStatusSerializer extends JsonSerializer<AgentWithLastStatus> {
        @Override
        public void serialize(AgentWithLastStatus value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeStartObject();
            gen.writeNumberField("id", value.agent.getId());
            gen.writeStringField("name", value.agent.getName());
            gen.writeStringField("os", value.agent.getOs());
            if(value.status != null) {
                AgentStatus status = value.status;
                gen.writeObjectFieldStart("status");
                String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(status.getId().getDate());
                gen.writeStringField("date", date);
                gen.writeNumberField("memory usage", status.getMemoryUsage());
                gen.writeNumberField("cpu usage", status.getCpuUsage());
                gen.writeNumberField("disk usage", status.getDiskUsage());
                gen.writeStringField("agent version", status.getAgentVersion());
                gen.writeEndObject();
            }
            gen.writeEndObject();
        }
    }
}
