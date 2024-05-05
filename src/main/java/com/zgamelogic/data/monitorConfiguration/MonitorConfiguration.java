package com.zgamelogic.data.monitorConfiguration;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "monitor_configurations")
@ToString
public class MonitorConfiguration {
    public enum Type { WEB, API }

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    @Enumerated(EnumType.STRING)
    private Type type;
    private Integer port;
    private String url;
    private String regex;

    public MonitorConfiguration(String name, Type type, int port, String url, String regex) {
        this.name = name;
        this.type = type;
        this.port = port;
        this.url = url;
        this.regex = regex;
    }

    public MonitorConfiguration(Long id){
        this.id = id;
    }

    public void update(MonitorConfiguration monitorConfiguration) {
        if(monitorConfiguration.getName() != null) name = monitorConfiguration.getName();
        if(monitorConfiguration.getType() != null) type = monitorConfiguration.getType();
        if(monitorConfiguration.getPort() != null) port = monitorConfiguration.getPort();
        if(monitorConfiguration.getUrl() != null) url = monitorConfiguration.getUrl();
        if(monitorConfiguration.getRegex() != null) regex = monitorConfiguration.getRegex();
    }
}