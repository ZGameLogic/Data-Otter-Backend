package com.zgamelogic.data.components;

import com.zgamelogic.data.entities.MonitorConfiguration;
import com.zgamelogic.data.repositories.MonitorConfigurationRepository;
import com.zgamelogic.data.repositories.backup.BackupMonitorConfigurationRepository;
import com.zgamelogic.data.repositories.primary.PrimaryMonitorConfigurationRepository;
import com.zgamelogic.services.DatabaseConnectionService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class DynamicMonitorConfigurationRepository extends DynamicRepository<MonitorConfiguration, Long, MonitorConfigurationRepository> {

    protected DynamicMonitorConfigurationRepository(PrimaryMonitorConfigurationRepository primaryRepository, BackupMonitorConfigurationRepository backupRepository, DatabaseConnectionService databaseConnectionService) {
        super(primaryRepository, backupRepository, databaseConnectionService);
    }

    public MonitorConfiguration save(MonitorConfiguration monitorConfiguration) {
        return executeOnBoth(repo -> repo.save(monitorConfiguration));
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
        executeWithFallbackVoid(repo -> repo.deleteById_MonitorConfigurationId(id));
    }

    private interface RepositoryOperation<T> {
        T execute(MonitorConfigurationRepository repo);
    }
}
