package com.zgamelogic.data.groupConfiguration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zgamelogic.data.monitorConfiguration.MonitorConfiguration;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.IOException;
import java.util.List;

@Entity
@Table(name = "MONITOR_GROUPS")
@Getter
@NoArgsConstructor
@ToString
@JsonSerialize(using = MonitorGroup.MonitorGroupSerialization.class)
public class MonitorGroup {
    @Id
    @GeneratedValue
    private long id;
    private String name;

    @ToString.Exclude
    @ManyToMany
    @Setter
    @JoinTable(name = "monitor_group_connections")
    private List<MonitorConfiguration> monitors;

    public MonitorGroup(String name){
        this.name = name;
    }

    public MonitorGroup(String name, List<Long> monitors) {
        this.name = name;
        this.monitors = monitors.stream().map(MonitorConfiguration::new).toList();
    }

    public MonitorGroup(long id){
        this.id = id;
    }

    public static class MonitorGroupSerialization extends JsonSerializer<MonitorGroup> {

        @Override
        public void serialize(MonitorGroup monitorGroup, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeNumberField("id", monitorGroup.getId());
            jsonGenerator.writeStringField("name", monitorGroup.getName());
            jsonGenerator.writeArrayFieldStart("monitors");
            if(monitorGroup.getMonitors() != null){
                for(MonitorConfiguration monitor : monitorGroup.getMonitors()){
                    jsonGenerator.writeNumber(monitor.getId());
                }
            }
            jsonGenerator.writeEndArray();
            jsonGenerator.writeEndObject();
        }
    }
}
