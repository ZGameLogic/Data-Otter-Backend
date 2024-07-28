package com.zgamelogic.data.monitorConfiguration;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.zgamelogic.data.application.Application;
import jakarta.persistence.*;
import lombok.*;

import java.io.IOException;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "monitor_configurations")
@ToString
@JsonDeserialize(using = MonitorConfiguration.MonitorConfigurationDeserialization.class)
public class MonitorConfiguration {
    public enum Type { WEB, API }
    @EmbeddedId
    private MonitorConfigurationId id;
    private String name;
    @Enumerated(EnumType.STRING)
    private Type type;
    private String url;
    private String regex;
    @Setter
    @Column(columnDefinition = "boolean default true")
    private boolean active;

    public MonitorConfiguration(long applicationId, String name, Type type, String url, String regex) {
        id = new MonitorConfigurationId(applicationId, 0L);
        this.name = name;
        this.type = type;
        this.url = url;
        this.regex = regex;
        active = true;
    }

    public MonitorConfiguration(long id, long applicationId){
        this.id = new MonitorConfigurationId(id, new Application(applicationId));
        active = true;
    }

    public MonitorConfiguration(){
        active = true;
    }

    public void update(MonitorConfiguration monitorConfiguration) {
        if(monitorConfiguration.getName() != null) name = monitorConfiguration.getName();
        if(monitorConfiguration.getType() != null) type = monitorConfiguration.getType();
        if(monitorConfiguration.getUrl() != null) url = monitorConfiguration.getUrl();
        if(monitorConfiguration.getRegex() != null) regex = monitorConfiguration.getRegex();
    }

    @Embeddable
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    @Getter
    @Setter
    @ToString
    public static class MonitorConfigurationId {
        @GeneratedValue
        @Column(name = "MONITOR_CONFIGURATION_ID")
        private Long monitorConfigurationId;

        @ManyToOne(cascade = CascadeType.REMOVE)
        @JoinColumn(name = "APPLICATION_ID", referencedColumnName = "ID")
        private Application application;

        public MonitorConfigurationId(long applicationId, long monitorConfigurationId) {
            this.monitorConfigurationId = monitorConfigurationId;
            application = new Application(applicationId);
        }
    }

    public static class MonitorConfigurationDeserialization extends JsonDeserializer<MonitorConfiguration> {
        @Override
        public MonitorConfiguration deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
            JsonNode node = p.getCodec().readTree(p);
            long applicationId = node.get("application id").asLong();
            String name = node.get("name").asText();
            MonitorConfiguration.Type type = MonitorConfiguration.Type.valueOf(node.get("type").asText());
            String url = node.get("url").asText();
            String regex = node.get("regex").asText();
            return new MonitorConfiguration(applicationId, name, type, url, regex);
        }
    }
}
