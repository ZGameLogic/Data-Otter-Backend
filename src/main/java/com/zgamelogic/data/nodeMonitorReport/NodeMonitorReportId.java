package com.zgamelogic.data.nodeMonitorReport;

import com.zgamelogic.data.monitorConfiguration.MonitorConfiguration;
import com.zgamelogic.data.nodeConfiguration.NodeConfiguration;
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

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "NODE_ID", referencedColumnName = "ID")
    private NodeConfiguration node;

    public NodeMonitorReportId(long monitorId, long nodeId){
        monitor = new MonitorConfiguration(monitorId);
        node = new NodeConfiguration(nodeId);
    }
}
