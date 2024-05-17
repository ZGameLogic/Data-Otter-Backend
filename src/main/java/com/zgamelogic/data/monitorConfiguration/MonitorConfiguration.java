package com.zgamelogic.data.monitorConfiguration;

import com.zgamelogic.data.groupConfiguration.MonitorGroup;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
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
    private String url;
    private String regex;

    @ToString.Exclude
    @ManyToMany(mappedBy = "monitors")
    @Setter
    private List<MonitorGroup> groups;

    public MonitorConfiguration(String name, Type type, String url, String regex) {
        this.name = name;
        this.type = type;
        this.url = url;
        this.regex = regex;
        groups = new ArrayList<>();
    }

    public MonitorConfiguration(String name, Type type, String url, String regex, List<Long> groups) {
        this.name = name;
        this.type = type;
        this.url = url;
        this.regex = regex;
        this.groups = groups.stream().map(MonitorGroup::new).toList();
    }

    public MonitorConfiguration(Long id){
        this.id = id;
    }

    public MonitorConfiguration(){
        groups = new ArrayList<>();
    }

    public void update(MonitorConfiguration monitorConfiguration) {
        if(monitorConfiguration.getName() != null) name = monitorConfiguration.getName();
        if(monitorConfiguration.getType() != null) type = monitorConfiguration.getType();
        if(monitorConfiguration.getUrl() != null) url = monitorConfiguration.getUrl();
        if(monitorConfiguration.getRegex() != null) regex = monitorConfiguration.getRegex();
    }
}
