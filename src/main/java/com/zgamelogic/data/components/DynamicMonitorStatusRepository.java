package com.zgamelogic.data.components;

import com.zgamelogic.data.entities.MonitorStatus;
import com.zgamelogic.data.repositories.MonitorStatusRepository;
import com.zgamelogic.data.repositories.backup.BackupMonitorStatusRepository;
import com.zgamelogic.data.repositories.primary.PrimaryMonitorStatusRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class DynamicMonitorStatusRepository {
    private final PrimaryMonitorStatusRepository primaryRepository;
    private final BackupMonitorStatusRepository backupRepository;

    private boolean primaryAvailable;
    private final List<RepositoryOperation> primaryCache;

    public DynamicMonitorStatusRepository(PrimaryMonitorStatusRepository primaryRepository, BackupMonitorStatusRepository backupRepository) {
        this.primaryRepository = primaryRepository;
        this.backupRepository = backupRepository;
        primaryAvailable = true;
        this.primaryCache = new ArrayList<>();
    }



    private <T> T executeWithFallback(DynamicMonitorStatusRepository.RepositoryOperation<T> operation, boolean cache) {
        try {
            if (primaryAvailable) {
                return operation.execute(primaryRepository);
            } else {
                primaryCache.add(operation);
            }
        } catch (Exception e) {
            primaryAvailable = false;
        }
        return operation.execute(backupRepository);
    }

    public void deleteAllById_Monitor_Id_Application_Id(long applicationId) {
        backupRepository.deleteAllById_Monitor_Id_Application_Id(applicationId);
        if (primaryAvailable) {
            primaryRepository.deleteAllById_Monitor_Id_Application_Id(applicationId);
        }
    }

    public void deleteAllById_monitor_id_monitorConfigurationIdAndId_Monitor_Id_Application_Id(long id, long appId) {
        backupRepository.deleteAllById_monitor_id_monitorConfigurationIdAndId_Monitor_Id_Application_Id(id, appId);
        if (primaryAvailable) {
            primaryRepository.deleteAllById_monitor_id_monitorConfigurationIdAndId_Monitor_Id_Application_Id(id, appId);
        }
    }

    public void deleteRecordsOlderThan(Date date) {
        backupRepository.deleteRecordsOlderThan(date);
        if (primaryAvailable) {
            primaryRepository.deleteRecordsOlderThan(date);
        }
    }

    public MonitorStatus save(MonitorStatus monitorStatus) {
        backupRepository.save(monitorStatus);
        return executeWithFallback(repo -> repo.save(monitorStatus), true);
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

    private interface RepositoryOperation<T> {
        T execute(MonitorStatusRepository repo);
    }

    @Scheduled(cron = "10 * * * * *")
    private void connectionTest(){
        if(primaryAvailable) return;
        try {
            primaryRepository.count();
            syncBackupToPrimary();
            primaryAvailable = true;
        } catch(Exception ignored) {}
    }

    private void syncBackupToPrimary(){
        primaryCache.forEach(cached -> cached.execute(primaryRepository));
        primaryCache.clear();
    }
}
