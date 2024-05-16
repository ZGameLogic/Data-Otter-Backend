package com.zgamelogic.data.groupConfiguration;

import com.zgamelogic.data.monitorConfiguration.MonitorConfiguration;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "MONITOR_GROUPS")
@Getter
@NoArgsConstructor
@ToString
public class MonitorGroup {
    @Id
    @GeneratedValue
    private long id;
    private String name;

    @ToString.Exclude
    @ManyToMany
    @JoinTable(name = "monitor_group_connections")
    private List<MonitorConfiguration> monitors;

    public MonitorGroup(String name){
        this.name = name;
    }
}
