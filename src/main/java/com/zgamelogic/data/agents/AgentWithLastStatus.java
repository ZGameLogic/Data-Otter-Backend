package com.zgamelogic.data.agents;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zgamelogic.data.agentHistory.AgentStatus;

import java.io.IOException;

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
                gen.writeObjectField("status", status);
            }
            gen.writeEndObject();
        }
    }
}
