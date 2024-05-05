package com.zgamelogic.data.nodeMonitorReport;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zgamelogic.data.monitorConfiguration.MonitorConfiguration;
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

    public NodeMonitorReport(long id, long nodeId, long milliseconds, boolean status, int attempts, int statusCode) {
        this(new MonitorConfiguration(id), nodeId, milliseconds, status, attempts, statusCode);
    }

    public NodeMonitorReport(MonitorConfiguration id, long nodeId, long milliseconds, boolean status, int attempts, int statusCode) {
        this.id = new NodeMonitorReportId(id, nodeId);
        this.milliseconds = milliseconds;
        this.status = status;
        this.attempts = attempts;
        this.statusCode = statusCode;
    }
}
