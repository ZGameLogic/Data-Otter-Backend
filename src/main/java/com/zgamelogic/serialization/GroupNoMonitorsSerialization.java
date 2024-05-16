package com.zgamelogic.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.zgamelogic.data.groupConfiguration.MonitorGroup;

import java.io.IOException;

public class GroupNoMonitorsSerialization extends JsonSerializer<MonitorGroup> {
    @Override
    public void serialize(MonitorGroup monitorGroup, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", monitorGroup.getId());
        jsonGenerator.writeStringField("name", monitorGroup.getName());
        jsonGenerator.writeEndObject();
    }
}
