package com.zgamelogic.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.zgamelogic.data.monitorConfiguration.MonitorConfigurationAndStatus;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class MonitorConfigStatusSerialization extends JsonSerializer<MonitorConfigurationAndStatus> {
    @Override
    public void serialize(MonitorConfigurationAndStatus data, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("name", data.monitorConfiguration().getName());
        jsonGenerator.writeStringField("type", data.monitorConfiguration().getType().name());
        jsonGenerator.writeNumberField("port", data.monitorConfiguration().getPort());
        jsonGenerator.writeStringField("url", data.monitorConfiguration().getUrl());
        jsonGenerator.writeStringField("regex", data.monitorConfiguration().getRegex());
        if(data.monitorStatus() != null){
            jsonGenerator.writeObjectFieldStart("recent status");
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(data.monitorStatus().getId().getDate());
            jsonGenerator.writeStringField("date recorded", date);
            jsonGenerator.writeNumberField("milliseconds", data.monitorStatus().getMilliseconds());
            jsonGenerator.writeBooleanField("status", data.monitorStatus().isStatus());
            jsonGenerator.writeNumberField("retries", data.monitorStatus().getRetries());
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndObject();
    }
}
