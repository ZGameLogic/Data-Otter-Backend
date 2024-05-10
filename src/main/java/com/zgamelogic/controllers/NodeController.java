package com.zgamelogic.controllers;

import com.zgamelogic.data.monitorConfiguration.MonitorConfigurationRepository;
import com.zgamelogic.data.nodeConfiguration.NodeConfiguration;
import com.zgamelogic.data.nodeConfiguration.NodeConfigurationRepository;
import com.zgamelogic.data.nodeMonitorReport.NodeMonitorReport;
import com.zgamelogic.data.nodeMonitorReport.NodeMonitorReportId;
import com.zgamelogic.data.nodeMonitorReport.NodeMonitorReportRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static com.zgamelogic.data.Constants.MASTER_NODE_NAME;

@Slf4j
@RestController
public class NodeController {
    private final NodeMonitorReportRepository nodeMonitorReportRepository;
    private final MonitorConfigurationRepository monitorConfigurationRepository;
    private final NodeConfigurationRepository nodeConfigurationRepository;

    public NodeController(NodeMonitorReportRepository nodeMonitorReportRepository, MonitorConfigurationRepository monitorConfigurationRepository, NodeConfigurationRepository nodeConfigurationRepository) {
        this.nodeMonitorReportRepository = nodeMonitorReportRepository;
        this.monitorConfigurationRepository = monitorConfigurationRepository;
        this.nodeConfigurationRepository = nodeConfigurationRepository;
    }

    @PostMapping("nodes/{nodeId}/report/{monitorId}")
    private ResponseEntity<NodeMonitorReport> report(
            @PathVariable("nodeId") long nodeId,
            @PathVariable("monitorId") long monitorId,
            @RequestBody NodeMonitorReport nodeMonitorReport
    ) {
        if(!monitorConfigurationRepository.existsById(monitorId)) return ResponseEntity.notFound().build();
        if(!nodeConfigurationRepository.existsById(nodeId)) return ResponseEntity.notFound().build();
        nodeMonitorReport.setId(new NodeMonitorReportId(monitorId, nodeId));
        NodeMonitorReport report = nodeMonitorReportRepository.save(nodeMonitorReport);
        return ResponseEntity.ok(report);
    }

    @PostMapping("nodes")
    private ResponseEntity<NodeConfiguration> nodes(@RequestBody NodeConfiguration nodeConfiguration){
        if(nodeConfiguration.getName() == null && !nodeConfiguration.getName().equals(MASTER_NODE_NAME)) return ResponseEntity.badRequest().build();
        NodeConfiguration saved = nodeConfigurationRepository.save(nodeConfiguration);
        return ResponseEntity.ok(saved);
    }

    @Bean("master-node")
    public NodeConfiguration masterNode(NodeConfigurationRepository nodeConfigurationRepository) {
        Optional<NodeConfiguration> nodeConfig = nodeConfigurationRepository.findByName(MASTER_NODE_NAME);
        NodeConfiguration masterNode = nodeConfig.orElseGet(() -> nodeConfigurationRepository.save(new NodeConfiguration(MASTER_NODE_NAME)));
        log.info("Master node id: {}", masterNode.getId());
        return masterNode;
    }
}
