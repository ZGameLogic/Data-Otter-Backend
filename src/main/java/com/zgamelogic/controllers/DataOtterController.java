package com.zgamelogic.controllers;

import com.zgamelogic.data.monitorConfiguration.MonitorConfigurationRepository;
import com.zgamelogic.data.monitorHistory.MonitorStatus;
import com.zgamelogic.data.monitorHistory.MonitorStatusRepository;
import com.zgamelogic.data.nodeConfiguration.NodeConfiguration;
import com.zgamelogic.data.nodeConfiguration.NodeConfigurationRepository;
import com.zgamelogic.data.nodeMonitorReport.NodeMonitorReport;
import com.zgamelogic.data.nodeMonitorReport.NodeMonitorReportRepository;
import com.zgamelogic.services.monitors.MonitorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.util.Comparator;
import java.util.List;
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
        masterNode = nodeConfig.orElseGet(() -> nodeConfigurationRepository.save(new NodeConfiguration(MASTER_NODE_NAME)));
        log.info("Master node id: {}", masterNode.getId());
    }

    /**
     * Run all the monitor configurations through and get their statuses, create node records of each
     */
    @Scheduled(cron = "5 * * * * *")
    public void dataOtterTasks(){
        monitorConfigurationRepository.findAll().forEach(monitorConfiguration ->
                monitorService.getMonitorStatus(monitorConfiguration).thenAccept(report ->
                    nodeMonitorReportRepository.save(new NodeMonitorReport(monitorConfiguration, masterNode, report)
                )
            )
        );
    }

    /**
     * Go through all the node records, determine if a monitor is down, and then add it to the history
     */
    @Scheduled(cron = "0 * * * * *")
    public void minuteJobs(){
        monitorConfigurationRepository.findAll().forEach(configuration -> {
            List<NodeMonitorReport> reports = nodeMonitorReportRepository.findAllById_MonitorId(configuration.getId());
            if(reports.stream().anyMatch(report -> !report.isStatus())){
                NodeMonitorReport badReport = reports.stream().filter(report -> !report.isStatus()).min(Comparator.comparingLong(NodeMonitorReport::getMilliseconds)
                        .thenComparingInt(NodeMonitorReport::getAttempts)).get();
                monitorStatusRepository.save(new MonitorStatus(configuration, badReport));
            } else {
                NodeMonitorReport topReport = reports.stream().min(Comparator.comparingLong(NodeMonitorReport::getMilliseconds)
                        .thenComparingInt(NodeMonitorReport::getAttempts)).get();
                monitorStatusRepository.save(new MonitorStatus(configuration, topReport));
            }
        });
        nodeMonitorReportRepository.deleteAll();
    }
}
