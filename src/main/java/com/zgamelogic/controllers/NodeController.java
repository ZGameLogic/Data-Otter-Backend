package com.zgamelogic.controllers;

import com.zgamelogic.data.monitorConfiguration.MonitorConfigurationRepository;
import com.zgamelogic.data.nodeMonitorReport.NodeMonitorReport;
import com.zgamelogic.data.nodeMonitorReport.NodeMonitorReportId;
import com.zgamelogic.data.nodeMonitorReport.NodeMonitorReportRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class NodeController {

    private final NodeMonitorReportRepository nodeMonitorReportRepository;
    private final MonitorConfigurationRepository monitorConfigurationRepository;

    public NodeController(NodeMonitorReportRepository nodeMonitorReportRepository, MonitorConfigurationRepository monitorConfigurationRepository) {
        this.nodeMonitorReportRepository = nodeMonitorReportRepository;
        this.monitorConfigurationRepository = monitorConfigurationRepository;
    }

    @PostMapping("nodes/{nodeId}/report/{monitorId}")
    private ResponseEntity<?> report(
            @PathVariable("nodeId") long nodeId,
            @PathVariable("monitorId") long monitorId,
            @RequestBody NodeMonitorReport nodeMonitorReport
    ) {
        if(!monitorConfigurationRepository.existsById(monitorId)) return ResponseEntity.notFound().build();
        nodeMonitorReport.setId(new NodeMonitorReportId(monitorId, nodeId));
        NodeMonitorReport report = nodeMonitorReportRepository.save(nodeMonitorReport);
        return ResponseEntity.ok(report);
    }
}
