package com.zgamelogic.dataotter.services;

import com.zgamelogic.dataotter.application.database.Application;
import com.zgamelogic.dataotter.application.database.ApplicationMonitorStatus;
import com.zgamelogic.dataotter.application.database.ApplicationRepository;
import com.zgamelogic.dataotter.monitor.database.MonitorStatus;
import com.zgamelogic.dataotter.monitor.database.MonitorStatusRepository;
import lombok.Getter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CacheService {
    private final ApplicationRepository applicationRepository;
    private final MonitorStatusRepository monitorStatusRepository;

    @Getter
    private List<ApplicationMonitorStatus> appMonitorStatuses;

    public CacheService(ApplicationRepository applicationRepository, MonitorStatusRepository monitorStatusRepository) {
        this.applicationRepository = applicationRepository;
        this.monitorStatusRepository = monitorStatusRepository;
        refreshCache();
    }

    @Scheduled(cron = "15 * * * * *")
    private void refreshCache(){
        List<Application> apps = applicationRepository.findAll();
        List<ApplicationMonitorStatus> appMonitorStatuses = new ArrayList<>();
        for(Application app : apps) {
            List<MonitorStatus> statuses = monitorStatusRepository.findByApplicationIdAndTopOneForEachMonitor(app.getId());
            appMonitorStatuses.add(new ApplicationMonitorStatus(app, statuses));
        }
        this.appMonitorStatuses = appMonitorStatuses;
    }
}
