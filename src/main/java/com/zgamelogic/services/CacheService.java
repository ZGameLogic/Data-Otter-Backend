package com.zgamelogic.services;

import com.zgamelogic.data.application.Application;
import com.zgamelogic.data.application.ApplicationMonitorStatus;
import com.zgamelogic.data.application.ApplicationRepository;
import com.zgamelogic.data.monitorHistory.MonitorStatus;
import com.zgamelogic.data.monitorHistory.MonitorStatusRepository;
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
