package com.zgamelogic.data.components;

import com.zgamelogic.data.entities.MonitorConfiguration;
import com.zgamelogic.data.events.DatabaseConnectionEvent;
import com.zgamelogic.data.repositories.MonitorConfigurationRepository;
import com.zgamelogic.data.repositories.backup.BackupMonitorConfigurationRepository;
import com.zgamelogic.data.repositories.primary.PrimaryMonitorConfigurationRepository;
import com.zgamelogic.services.DatabaseConnectionService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class DynamicMonitorConfigurationRepository {
    private final PrimaryMonitorConfigurationRepository primaryMonitorConfigurationRepository;
    private final BackupMonitorConfigurationRepository backupMonitorConfigurationRepository;
    private final DatabaseConnectionService databaseConnectionService;

    private final List<RepositoryOperation> primaryCache;

    public DynamicMonitorConfigurationRepository(PrimaryMonitorConfigurationRepository primaryMonitorConfigurationRepository, BackupMonitorConfigurationRepository backupMonitorConfigurationRepository, DatabaseConnectionService databaseConnectionService) {
        this.primaryMonitorConfigurationRepository = primaryMonitorConfigurationRepository;
        this.backupMonitorConfigurationRepository = backupMonitorConfigurationRepository;
        this.databaseConnectionService = databaseConnectionService;
        primaryCache = new ArrayList<>();
    }

    private <T> T executeWithFallback(DynamicMonitorConfigurationRepository.RepositoryOperation<T> operation, boolean cache) {
        try {
            if (databaseConnectionService.isDatabaseConnected()) {
                return operation.execute(primaryMonitorConfigurationRepository);
            } else {
                primaryCache.add(operation);
            }
        } catch (Exception e) {
            databaseConnectionService.setDatabaseConnected(false);
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
            if (databaseConnectionService.isDatabaseConnected()) {
                primaryMonitorConfigurationRepository.deleteById_MonitorConfigurationId(id);
            }
        } catch (Exception e) {
            databaseConnectionService.setDatabaseConnected(false);
        }
        backupMonitorConfigurationRepository.deleteById_MonitorConfigurationId(id);
    }

    private interface RepositoryOperation<T> {
        T execute(MonitorConfigurationRepository repo);
    }

    @EventListener
    private void connectionEvent(DatabaseConnectionEvent event){
        if(event.isConnected()) syncBackupToPrimary();
    }

    private void syncBackupToPrimary(){
        primaryCache.forEach(cached -> cached.execute(primaryMonitorConfigurationRepository));
        primaryCache.clear();
    }
}
