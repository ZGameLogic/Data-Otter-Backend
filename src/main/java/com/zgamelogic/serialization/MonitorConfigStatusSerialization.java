package com.zgamelogic.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.zgamelogic.data.monitorConfiguration.MonitorConfigurationAndStatus;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.text.SimpleDateFormat;

@Slf4j
public class MonitorConfigStatusSerialization extends JsonSerializer<MonitorConfigurationAndStatus> {
    @Override
    public void serialize(MonitorConfigurationAndStatus data, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", data.monitorConfiguration().getId());
        jsonGenerator.writeStringField("name", data.monitorConfiguration().getName());
        jsonGenerator.writeStringField("type", data.monitorConfiguration().getType().name());
        jsonGenerator.writeStringField("url", data.monitorConfiguration().getUrl());
        jsonGenerator.writeStringField("regex", data.monitorConfiguration().getRegex());
        jsonGenerator.writeBooleanField("active", data.monitorConfiguration().isActive());
        if(data.monitorStatus() != null){
            jsonGenerator.writeObjectFieldStart("status");
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(data.monitorStatus().getId().getDate());
            jsonGenerator.writeStringField("date recorded", date);
            jsonGenerator.writeNumberField("milliseconds", data.monitorStatus().getMilliseconds());
            jsonGenerator.writeBooleanField("status", data.monitorStatus().isStatus());
            jsonGenerator.writeNumberField("attempts", data.monitorStatus().getAttempts());
            jsonGenerator.writeNumberField("status code", data.monitorStatus().getStatusCode());
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndObject();
    }
}
