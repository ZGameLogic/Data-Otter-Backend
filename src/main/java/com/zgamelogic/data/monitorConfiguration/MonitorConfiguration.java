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

    public MonitorConfiguration(long id){
        this.id = id;
    }
}
