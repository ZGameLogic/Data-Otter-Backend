package com.zgamelogic.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.zgamelogic.data.monitorHistory.MonitorStatus;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class MonitorStatusSerialization extends JsonSerializer<MonitorStatus> {
    @Override
    public void serialize(MonitorStatus monitorStatus, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        if(monitorStatus.getId() != null) {
            if(monitorStatus.getId().getMonitor() != null){
                if(monitorStatus.getId().getMonitor().getId() != null)
                    jsonGenerator.writeNumberField("monitor id", monitorStatus.getId().getMonitor().getId());
                if(monitorStatus.getId().getDate() != null) {
                    String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(monitorStatus.getId().getDate());
                    jsonGenerator.writeStringField("date", date);
                }
            }
        }
        jsonGenerator.writeNumberField("milliseconds", monitorStatus.getMilliseconds());
        jsonGenerator.writeBooleanField("status", monitorStatus.isStatus());
        jsonGenerator.writeNumberField("retries", monitorStatus.getRetries());
        jsonGenerator.writeEndObject();
    }
}
