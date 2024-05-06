package com.zgamelogic.data.nodeMonitorReport;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zgamelogic.data.monitorConfiguration.MonitorConfiguration;
import com.zgamelogic.data.nodeConfiguration.NodeConfiguration;
import com.zgamelogic.services.monitors.MonitorStatusReport;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "node_reports")
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
        this(new MonitorConfiguration(id), new NodeConfiguration(nodeId), milliseconds, status, attempts, statusCode);
    }

    public NodeMonitorReport(MonitorConfiguration id, NodeConfiguration nodeId, long milliseconds, boolean status, int attempts, int statusCode) {
        this.id = new NodeMonitorReportId(id, nodeId);
        this.milliseconds = milliseconds;
        this.status = status;
        this.attempts = attempts;
        this.statusCode = statusCode;
    }
}
