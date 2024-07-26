package com.zgamelogic.data.monitorConfiguration;

import com.zgamelogic.data.application.Application;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@Table(name = "monitor_configurations")
@ToString
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

    public MonitorConfiguration(String name, Type type, String url, String regex) {
        this.name = name;
        this.type = type;
        this.url = url;
        this.regex = regex;
        active = true;
    }

    public MonitorConfiguration(String name, Type type, String url, String regex, List<Long> groups) {
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
    public static class MonitorConfigurationId {
        @GeneratedValue
        @Column(name = "MONITOR_CONFIGURATION_ID")
        private Long monitorConfigurationId;

        @ManyToOne(cascade = CascadeType.REMOVE)
        @JoinColumn(name = "APPLICATION_ID", referencedColumnName = "ID")
        private Application application;
    }
}
