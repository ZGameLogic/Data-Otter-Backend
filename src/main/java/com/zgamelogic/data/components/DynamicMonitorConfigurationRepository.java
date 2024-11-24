package com.zgamelogic.data.components;

import com.zgamelogic.data.entities.MonitorConfiguration;
import com.zgamelogic.data.repositories.MonitorConfigurationRepository;
import com.zgamelogic.data.repositories.backup.BackupMonitorConfigurationRepository;
import com.zgamelogic.data.repositories.primary.PrimaryMonitorConfigurationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class DynamicMonitorConfigurationRepository {
    private final PrimaryMonitorConfigurationRepository primaryMonitorConfigurationRepository;
    private final BackupMonitorConfigurationRepository backupMonitorConfigurationRepository;

    private boolean primaryAvailable;
    private final List<RepositoryOperation> primaryCache;

    public DynamicMonitorConfigurationRepository(PrimaryMonitorConfigurationRepository primaryMonitorConfigurationRepository, BackupMonitorConfigurationRepository backupMonitorConfigurationRepository) {
        this.primaryMonitorConfigurationRepository = primaryMonitorConfigurationRepository;
        this.backupMonitorConfigurationRepository = backupMonitorConfigurationRepository;
        primaryAvailable = true;
        primaryCache = new ArrayList<>();
    }





    private <T> T executeWithFallback(DynamicMonitorConfigurationRepository.RepositoryOperation<T> operation, boolean cache) {
        try {
            if (primaryAvailable) {
                return operation.execute(primaryMonitorConfigurationRepository);
            } else {
                primaryCache.add(operation);
            }
        } catch (Exception e) {
            primaryAvailable = false;
        }
        return operation.execute(backupMonitorConfigurationRepository);
    }

    public MonitorConfiguration save(MonitorConfiguration monitorConfiguration) {
        backupMonitorConfigurationRepository.save(monitorConfiguration);
        return executeWithFallback(repo -> repo.save(monitorConfiguration), true);
    }

    public Optional<MonitorConfiguration> findById_MonitorConfigurationIdAndId_Application_Id(Long monitorConfigurationId, long appId) {
        return executeWithFallback(repo -> repo.findById_MonitorConfigurationIdAndId_Application_Id(monitorConfigurationId, appId), false);
    }

    public List<MonitorConfiguration> findAllByActiveIsTrue() {
        return executeWithFallback(MonitorConfigurationRepository::findAllByActiveIsTrue, false);
    }

    public List<MonitorConfiguration> findAll() {
        return executeWithFallback(MonitorConfigurationRepository::findAll, false);
    }

    public boolean existsById_MonitorConfigurationIdAndId_Application_Id(long id, long appId) {
        return executeWithFallback(repo -> repo.existsById_MonitorConfigurationIdAndId_Application_Id(id, appId), false);
    }

    public void deleteById_MonitorConfigurationId(long id) {
        try {
            if (primaryAvailable) {
                primaryMonitorConfigurationRepository.deleteById_MonitorConfigurationId(id);
            }
        } catch (Exception e) {
            primaryAvailable = false;
        }
        backupMonitorConfigurationRepository.deleteById_MonitorConfigurationId(id);
    }

    private interface RepositoryOperation<T> {
        T execute(MonitorConfigurationRepository repo);
    }

    @Scheduled(cron = "10 * * * * *")
    private void connectionTest(){
        if(primaryAvailable) return;
        try {
            primaryMonitorConfigurationRepository.count();
            syncBackupToPrimary();
            primaryAvailable = true;
        } catch(Exception ignored) {}
    }

    private void syncBackupToPrimary(){
        primaryCache.forEach(cached -> cached.execute(primaryMonitorConfigurationRepository));
        primaryCache.clear();
    }
}
