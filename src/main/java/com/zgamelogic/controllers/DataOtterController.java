package com.zgamelogic.controllers;

import com.zgamelogic.data.agentHistory.AgentStatusRepository;
import com.zgamelogic.data.devices.DeviceRepository;
import com.zgamelogic.data.monitorConfiguration.MonitorConfigurationRepository;
import com.zgamelogic.data.monitorHistory.MonitorStatus;
import com.zgamelogic.data.monitorHistory.MonitorStatusRepository;
import com.zgamelogic.data.nodeConfiguration.NodeConfiguration;
import com.zgamelogic.data.nodeMonitorReport.NodeMonitorReport;
import com.zgamelogic.data.nodeMonitorReport.NodeMonitorReportRepository;
import com.zgamelogic.services.apns.ApplePushNotification;
import com.zgamelogic.services.apns.ApplePushNotificationService;
import com.zgamelogic.services.monitors.MonitorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class DataOtterController {
    private final MonitorConfigurationRepository monitorConfigurationRepository;
    private final MonitorStatusRepository monitorStatusRepository;
    private final NodeMonitorReportRepository nodeMonitorReportRepository;
    private final MonitorService monitorService;
    private final NodeConfiguration masterNode;
    private final ApplePushNotificationService apns;
    private final DeviceRepository deviceRepository;
    private final AgentStatusRepository agentStatusRepository;

    public DataOtterController(
            MonitorConfigurationRepository monitorConfigurationRepository,
            MonitorStatusRepository monitorStatusRepository,
            NodeMonitorReportRepository nodeMonitorReportRepository,
            MonitorService monitorService,
            @Qualifier("master-node") NodeConfiguration masterNode,
            ApplePushNotificationService apns,
            DeviceRepository deviceRepository,
            AgentStatusRepository agentStatusRepository
    ) {
        this.monitorConfigurationRepository = monitorConfigurationRepository;
        this.monitorStatusRepository = monitorStatusRepository;
        this.nodeMonitorReportRepository = nodeMonitorReportRepository;
        this.monitorService = monitorService;
        this.masterNode = masterNode;
        this.apns = apns;
        this.deviceRepository = deviceRepository;
        this.agentStatusRepository = agentStatusRepository;
        log.info("Performing initial cleanup");
        cleanup();
        log.info("Cleanup completed");
    }

    /**
     * Run all the monitor configurations through and get their statuses, create node records of each
     */
    @Scheduled(cron = "5 * * * * *")
    public void dataOtterTasks(){
        monitorConfigurationRepository.findAllByActiveIsTrue().forEach(monitorConfiguration ->
                monitorService.getMonitorStatus(monitorConfiguration).thenAccept(report ->
                    nodeMonitorReportRepository.save(new NodeMonitorReport(monitorConfiguration, masterNode, report)
                )
            )
        );
    }

    @Scheduled(cron = "0 * * * * *")
    public void cleanup(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        Date oneWeekAgo = calendar.getTime();
        monitorStatusRepository.deleteRecordsOlderThan(oneWeekAgo);
        agentStatusRepository.deleteRecordsOlderThan(oneWeekAgo);
    }

    /**
     * Go through all the node records, determine if a monitor is down, and then add it to the history
     */
    @Scheduled(cron = "0 * * * * *")
    public void minuteJobs(){
        List<MonitorStatus> changedMonitors = new ArrayList<>();
        monitorConfigurationRepository.findAllByActiveIsTrue().forEach(configuration -> {
            List<NodeMonitorReport> reports = nodeMonitorReportRepository.findAllById_Monitor_Id_Application_IdAndId_Monitor_Id_MonitorConfigurationId(configuration.getId().getApplication().getId(), configuration.getId().getMonitorConfigurationId());
            if(reports.isEmpty()) return;
            MonitorStatus monitorStatus;
            if(reports.size() == 1){
                monitorStatus = new MonitorStatus(configuration, reports.get(0));
            } else if(reports.stream().anyMatch(report -> !report.isStatus())){
                NodeMonitorReport badReport = reports.stream().filter(report -> !report.isStatus()).min(Comparator.comparingLong(NodeMonitorReport::getMilliseconds)
                        .thenComparingInt(NodeMonitorReport::getAttempts)).get();
                monitorStatus = new MonitorStatus(configuration, badReport);
            } else {
                NodeMonitorReport topReport = reports.stream().min(Comparator.comparingLong(NodeMonitorReport::getMilliseconds)
                        .thenComparingInt(NodeMonitorReport::getAttempts)).get();
                monitorStatus = new MonitorStatus(configuration, topReport);
            }
            long monitorId = monitorStatus.getId().getMonitor().getId().getMonitorConfigurationId();
            long applicationId = monitorStatus.getId().getMonitor().getId().getApplication().getId();
            Optional<MonitorStatus> mostRecentStatus = monitorStatusRepository.findTopById_Monitor_Id_MonitorConfigurationIdAndId_Monitor_Id_Application_IdOrderById_DateDesc(monitorId, applicationId);
            mostRecentStatus.ifPresent(previousStatus -> {
                if(previousStatus.isStatus() != monitorStatus.isStatus()) {
                    changedMonitors.add(monitorStatus);
                }
            });
            monitorStatusRepository.save(monitorStatus);
        });
        nodeMonitorReportRepository.deleteAll();
        if(changedMonitors.isEmpty()) return;
        String subtitle;
        if(changedMonitors.size() > 1){
            subtitle = String.format("%s monitors are alerting", changedMonitors.stream()
                    .map(monitor -> monitor.getId().getMonitor().getName())
                    .collect(Collectors.joining(","))
            );
        } else {
            subtitle = String.format("%s monitor is alerting", changedMonitors.get(0).getId().getMonitor().getName());
        }
        String body = changedMonitors.stream().map(monitor -> String.format("%s : %s", monitor.getId().getMonitor().getName(), monitor.isStatus() ? "up": "down")).collect(Collectors.joining("\n"));
        ApplePushNotification notification = new ApplePushNotification("Data Otter", subtitle, body);
        deviceRepository.findAll().forEach(device -> apns.sendNotification(device.getId(), notification));
    }
}
