package com.zgamelogic.data.components;

import com.zgamelogic.data.entities.MonitorStatus;
import com.zgamelogic.data.repositories.MonitorStatusRepository;
import com.zgamelogic.data.repositories.backup.BackupMonitorStatusRepository;
import com.zgamelogic.data.repositories.primary.PrimaryMonitorStatusRepository;
import com.zgamelogic.services.DatabaseConnectionService;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class DynamicMonitorStatusRepository extends DynamicRepository<MonitorStatus, MonitorStatus.MonitorStatusId, MonitorStatusRepository> {

    protected DynamicMonitorStatusRepository(PrimaryMonitorStatusRepository primaryRepository, BackupMonitorStatusRepository backupRepository, DatabaseConnectionService databaseConnectionService) {
        super(primaryRepository, backupRepository, databaseConnectionService);
    }

    public void deleteAllById_Monitor_Id_Application_Id(long applicationId) {
        executeOnBothVoid(repo -> repo.deleteAllById_Monitor_Id_Application_Id(applicationId), true);
    }

    public void deleteAllById_monitor_id_monitorConfigurationIdAndId_Monitor_Id_Application_Id(long id, long appId) {
        executeOnBothVoid(repo -> repo.deleteAllById_monitor_id_monitorConfigurationIdAndId_Monitor_Id_Application_Id(id, appId), true);
    }

    public void deleteRecordsOlderThan(Date date) {
        executeOnBothVoid(repo -> repo.deleteRecordsOlderThan(date), true);
    }

    public List<MonitorStatus> findByApplicationIdAndTopOneForEachMonitor(Long id) {
        return executeWithFallback(repo -> repo.findByApplicationIdAndTopOneForEachMonitor(id), false);
    }

    public Optional<MonitorStatus> findTopById_Monitor_Id_MonitorConfigurationIdAndId_Monitor_Id_Application_IdOrderById_Date(Long monitorConfigurationId, Long id) {
        return executeWithFallback(repo -> repo.findTopById_Monitor_Id_MonitorConfigurationIdAndId_Monitor_Id_Application_IdOrderById_Date(monitorConfigurationId, id), false);
    }

    public List<MonitorStatus> findByMonitorIdAndDateBetween(long id, Date start, Date end, long appId) {
        return executeWithFallback(repo -> repo.findByMonitorIdAndDateBetween(id, start, end, appId), false);
    }
}
