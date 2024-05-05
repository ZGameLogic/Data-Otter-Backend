package com.zgamelogic.data.nodeMonitorReport;

import com.zgamelogic.data.monitorConfiguration.MonitorConfiguration;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class NodeMonitorReportId {
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "MONITOR_ID", referencedColumnName = "ID")
    private MonitorConfiguration monitor;
    private long nodeId;

    public NodeMonitorReportId(long monitorId, long nodeId){
        monitor = new MonitorConfiguration(monitorId);
        this.nodeId = nodeId;
    }
}
