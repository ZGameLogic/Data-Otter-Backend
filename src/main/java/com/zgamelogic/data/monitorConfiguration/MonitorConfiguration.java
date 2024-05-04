package com.zgamelogic.data.monitorConfiguration;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "monitor_configurations")
public class MonitorConfiguration {
    public enum Type { WEB, API }

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    @Enumerated(EnumType.STRING)
    private Type type;
    private int port;
    private String url;
    private String regex;

    public MonitorConfiguration(String name, Type type, int port, String url, String regex) {
        this.name = name;
        this.type = type;
        this.port = port;
        this.url = url;
        this.regex = regex;
    }
}
