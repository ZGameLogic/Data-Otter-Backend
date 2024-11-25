package com.zgamelogic.data.components;

import com.zgamelogic.data.entities.Device;
import com.zgamelogic.data.events.DatabaseConnectionEvent;
import com.zgamelogic.data.repositories.DeviceRepository;
import com.zgamelogic.data.repositories.backup.BackupDeviceRepository;
import com.zgamelogic.data.repositories.primary.PrimaryDeviceRepository;
import com.zgamelogic.services.DatabaseConnectionService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DynamicDeviceRepository {
    private final PrimaryDeviceRepository primaryDeviceRepository;
    private final BackupDeviceRepository backupDeviceRepository;
    private final DatabaseConnectionService databaseConnectionService;

    private final List<DynamicDeviceRepository.RepositoryOperation> primaryCache;

    public DynamicDeviceRepository(PrimaryDeviceRepository primaryDeviceRepository, BackupDeviceRepository backupDeviceRepository, DatabaseConnectionService databaseConnectionService) {
        this.primaryDeviceRepository = primaryDeviceRepository;
        this.backupDeviceRepository = backupDeviceRepository;
        this.databaseConnectionService = databaseConnectionService;
        primaryCache = new ArrayList<>();
    }

    public List<Device> findAll() {
        return executeWithFallback(DeviceRepository::findAll, false);
    }

    public void save(Device device) {
        backupDeviceRepository.save(device);
        executeWithFallback(repo -> repo.save(device), true);
    }

    public void deleteById(String deviceId) {
        try {
            if (databaseConnectionService.isDatabaseConnected()) {
                primaryDeviceRepository.deleteById(deviceId);
            }
        } catch (Exception e) {
            databaseConnectionService.setDatabaseConnected(false);
        }
        backupDeviceRepository.deleteById(deviceId);
    }

    private <T> T executeWithFallback(DynamicDeviceRepository.RepositoryOperation<T> operation, boolean cache) {
        try {
            if (databaseConnectionService.isDatabaseConnected()) {
                return operation.execute(primaryDeviceRepository);
            } else {
                primaryCache.add(operation);
            }
        } catch (Exception e) {
            databaseConnectionService.setDatabaseConnected(false);
        }
        return operation.execute(backupDeviceRepository);
    }

    @EventListener
    private void connectionEvent(DatabaseConnectionEvent event){
        if(event.isConnected()) syncBackupToPrimary();
    }

    protected interface RepositoryOperation<T> {
        T execute(DeviceRepository repo);
    }

    private void syncBackupToPrimary(){
        primaryCache.forEach(cached -> cached.execute(primaryDeviceRepository));
        primaryCache.clear();
    }
    
}
