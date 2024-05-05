package com.zgamelogic.controllers;

import com.zgamelogic.data.monitorConfiguration.MonitorConfigurationRepository;
import com.zgamelogic.data.monitorHistory.MonitorStatusRepository;
import com.zgamelogic.data.nodeMonitorReport.NodeMonitorReport;
import com.zgamelogic.data.nodeMonitorReport.NodeMonitorReportRepository;
import com.zgamelogic.services.monitors.MonitorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class DataOtterController {
    private final MonitorConfigurationRepository monitorConfigurationRepository;
    private final MonitorStatusRepository monitorStatusRepository;
    private final NodeMonitorReportRepository nodeMonitorReportRepository;
    private final MonitorService monitorService;

    public DataOtterController(MonitorConfigurationRepository monitorConfigurationRepository, MonitorStatusRepository monitorStatusRepository, NodeMonitorReportRepository nodeMonitorReportRepository, MonitorService monitorService) {
        this.monitorConfigurationRepository = monitorConfigurationRepository;
        this.monitorStatusRepository = monitorStatusRepository;
        this.nodeMonitorReportRepository = nodeMonitorReportRepository;
        this.monitorService = monitorService;
    }

    /**
     * Run all the monitor configurations through and get their statuses, create node records of each
     */
    @Scheduled(cron = "55 * * * * *")
    public void preMinuteJobs(){
        // TODO idk if the node id for this master app will be 0, ill be sure to figure that out though
        monitorConfigurationRepository.findAll().forEach(monitorConfiguration ->
                monitorService.getMonitorStatus(monitorConfiguration).thenAccept(report ->
                        nodeMonitorReportRepository.save(new NodeMonitorReport(monitorConfiguration, 0, report))
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
