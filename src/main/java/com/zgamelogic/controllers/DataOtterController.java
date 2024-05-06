package com.zgamelogic.controllers;

import com.zgamelogic.data.monitorConfiguration.MonitorConfigurationRepository;
import com.zgamelogic.data.monitorHistory.MonitorStatusRepository;
import com.zgamelogic.data.nodeConfiguration.NodeConfiguration;
import com.zgamelogic.data.nodeConfiguration.NodeConfigurationRepository;
import com.zgamelogic.data.nodeMonitorReport.NodeMonitorReport;
import com.zgamelogic.data.nodeMonitorReport.NodeMonitorReportRepository;
import com.zgamelogic.services.monitors.MonitorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.util.Optional;

import static com.zgamelogic.data.Constants.MASTER_NODE_NAME;

@Slf4j
@Controller
public class DataOtterController {
    private final MonitorConfigurationRepository monitorConfigurationRepository;
    private final MonitorStatusRepository monitorStatusRepository;
    private final NodeMonitorReportRepository nodeMonitorReportRepository;
    private final MonitorService monitorService;
    private final NodeConfiguration masterNode;

    public DataOtterController(
            MonitorConfigurationRepository monitorConfigurationRepository,
            MonitorStatusRepository monitorStatusRepository,
            NodeMonitorReportRepository nodeMonitorReportRepository,
            NodeConfigurationRepository nodeConfigurationRepository,
            MonitorService monitorService
    ) {
        this.monitorConfigurationRepository = monitorConfigurationRepository;
        this.monitorStatusRepository = monitorStatusRepository;
        this.nodeMonitorReportRepository = nodeMonitorReportRepository;
        this.monitorService = monitorService;
        Optional<NodeConfiguration> nodeConfig = nodeConfigurationRepository.findByName(MASTER_NODE_NAME);
        masterNode = nodeConfig.orElse(nodeConfigurationRepository.save(new NodeConfiguration(MASTER_NODE_NAME)));
        log.info("Master node id: {}", masterNode.getId());
    }

    /**
     * Run all the monitor configurations through and get their statuses, create node records of each
     */
    @Scheduled(cron = "55 * * * * *")
    public void preMinuteJobs(){
        monitorConfigurationRepository.findAll().forEach(monitorConfiguration ->
                monitorService.getMonitorStatus(monitorConfiguration).thenAccept(report ->
                        nodeMonitorReportRepository.save(new NodeMonitorReport(monitorConfiguration, masterNode, report))
                )
        );
    }

    /**
     * Go through all the node records, determine if a monitor is down, and then add it to the history
     */
    @Scheduled(cron = "0 * * * * *")
    public void minuteJobs(){
        // TODO go through all the node reports, consolidate them to make one Status History, and save that. Then delete all the node reports
    }
}
