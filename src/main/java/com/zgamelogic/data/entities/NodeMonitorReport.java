package com.zgamelogic.data.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zgamelogic.services.monitors.MonitorStatusReport;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "NODE_REPORTS")
public class NodeMonitorReport {
    @EmbeddedId
    @JsonIgnore
    private NodeMonitorReportId id;
    private long milliseconds;
    private boolean status;
    private int attempts;
    private int statusCode;

    public NodeMonitorReport(MonitorConfiguration id, NodeConfiguration nodeId, MonitorStatusReport report) {
        this(id, nodeId, report.milliseconds(), report.status(), report.attempts(), report.statusCode());
    }

    public NodeMonitorReport(long id, long nodeId, long milliseconds, boolean status, int attempts, int statusCode) {
        this(new MonitorConfiguration(id, 0L), new NodeConfiguration(nodeId), milliseconds, status, attempts, statusCode);
    }

    public NodeMonitorReport(MonitorConfiguration id, NodeConfiguration nodeId, long milliseconds, boolean status, int attempts, int statusCode) {
        this.id = new NodeMonitorReportId(id, nodeId);
        this.milliseconds = milliseconds;
        this.status = status;
        this.attempts = attempts;
        this.statusCode = statusCode;
    }

    @Embeddable
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class NodeMonitorReportId {
        @MapsId
        @ManyToOne(cascade = CascadeType.ALL)
        @JoinColumns({
                @JoinColumn(name = "MONITOR_CONFIGURATION_ID", referencedColumnName = "MONITOR_CONFIGURATION_ID"),
                @JoinColumn(name = "APPLICATION_ID", referencedColumnName = "APPLICATION_ID")
        })
        private MonitorConfiguration monitor;

        @ManyToOne(cascade = CascadeType.ALL)
        @JoinColumn(name = "NODE_ID", referencedColumnName = "ID")
        private NodeConfiguration node;

        public NodeMonitorReportId(long applicationId, long monitorId, long nodeId){
            monitor = new MonitorConfiguration(monitorId, applicationId);
            node = new NodeConfiguration(nodeId);
        }
    }

}
