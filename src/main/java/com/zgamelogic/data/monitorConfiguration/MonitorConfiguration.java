package com.zgamelogic.data.monitorConfiguration;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "monitor_configurations")
public class MonitorConfiguration {
    public enum Type { WEB, API }

    @Id
    @GeneratedValue
    private Long id;
    @Enumerated(EnumType.STRING)
    private Type type;
    private int port;
    private String url;
    private String regex;
}
