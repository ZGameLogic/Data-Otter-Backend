package com.zgamelogic.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.zgamelogic.data.monitorConfiguration.MonitorConfiguration;

import java.io.IOException;

public class MonitorNoGroupSerialization extends JsonSerializer<MonitorConfiguration> {
    @Override
    public void serialize(MonitorConfiguration config, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", config.getId());
        jsonGenerator.writeStringField("name", config.getName());
        jsonGenerator.writeStringField("type", config.getType().name());
        jsonGenerator.writeStringField("url", config.getUrl());
        jsonGenerator.writeStringField("regex", config.getRegex());
        jsonGenerator.writeEndObject();
    }
}
