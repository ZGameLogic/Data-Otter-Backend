package com.zgamelogic.data.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zgamelogic.data.entities.Application;
import com.zgamelogic.data.entities.MonitorConfiguration;
import com.zgamelogic.data.entities.MonitorStatus;
import com.zgamelogic.data.entities.Tag;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

@JsonSerialize(using = ApplicationMonitorStatus.ApplicationMonitorStatusSerializer.class)
public record ApplicationMonitorStatus(Application application, List<MonitorStatus> statuses) {

    public static class ApplicationMonitorStatusSerializer extends JsonSerializer<ApplicationMonitorStatus> {

        @Override
        public void serialize(ApplicationMonitorStatus applicationMonitorStatus, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
            Application app = applicationMonitorStatus.application();
            gen.writeStartObject();
            gen.writeNumberField("id", app.getId());
            gen.writeStringField("name", app.getName());
            gen.writeStringField("description", app.getDescription());
            gen.writeArrayFieldStart("monitor ids");
            for(MonitorConfiguration monitor : app.getMonitors()) gen.writeNumber(monitor.getId().getMonitorConfigurationId());
            gen.writeEndArray();
            gen.writeArrayFieldStart("tags");
            for(Tag tag : app.getTags()) gen.writeString(tag.getName());
            gen.writeEndArray();
            if(applicationMonitorStatus.statuses() != null && !applicationMonitorStatus.statuses().isEmpty()){
                boolean overall = applicationMonitorStatus.statuses().stream()
                        .map(MonitorStatus::isStatus)
                        .reduce(true, (acc, status) -> acc && status);
                gen.writeBooleanField("status", overall);
                gen.writeArrayFieldStart("monitor statuses");
                for(MonitorStatus status: applicationMonitorStatus.statuses()) {
                    gen.writeStartObject();
                    String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(status.getId().getDate());
                    gen.writeNumberField("monitor id", status.getId().getMonitor().getId().getMonitorConfigurationId());
                    gen.writeStringField("date recorded", date);
                    gen.writeNumberField("milliseconds", status.getMilliseconds());
                    gen.writeBooleanField("status", status.isStatus());
                    gen.writeNumberField("attempts", status.getAttempts());
                    gen.writeNumberField("status code", status.getStatusCode());
                    gen.writeEndObject();
                }
                gen.writeEndArray();
            }
            gen.writeEndObject();
        }
    }
}
