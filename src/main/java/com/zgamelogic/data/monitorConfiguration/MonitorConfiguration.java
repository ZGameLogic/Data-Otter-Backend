package com.zgamelogic.data.monitorConfiguration;

import com.zgamelogic.data.MonitorStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

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

    @ElementCollection
    @CollectionTable(name="status_history")
    private List<MonitorStatus> statusHistory;
}
