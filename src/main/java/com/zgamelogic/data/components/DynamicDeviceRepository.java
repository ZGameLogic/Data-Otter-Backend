package com.zgamelogic.data.components;

import com.zgamelogic.data.entities.Device;
import com.zgamelogic.data.repositories.DeviceRepository;
import com.zgamelogic.data.repositories.backup.BackupDeviceRepository;
import com.zgamelogic.data.repositories.primary.PrimaryDeviceRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DynamicDeviceRepository {
    private final PrimaryDeviceRepository primaryDeviceRepository;
    private final BackupDeviceRepository backupDeviceRepository;

    private boolean primaryAvailable;
    private final List<RepositoryOperation> primaryCache;

    public DynamicDeviceRepository(PrimaryDeviceRepository primaryDeviceRepository, BackupDeviceRepository backupDeviceRepository) {
        this.primaryDeviceRepository = primaryDeviceRepository;
        this.backupDeviceRepository = backupDeviceRepository;
        primaryAvailable = true;
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
            if (primaryAvailable) {
                primaryDeviceRepository.deleteById(deviceId);
            }
        } catch (Exception e) {
            primaryAvailable = false;
        }
        backupDeviceRepository.deleteById(deviceId);
    }

    private <T> T executeWithFallback(DynamicDeviceRepository.RepositoryOperation<T> operation, boolean cache) {
        try {
            if (primaryAvailable) {
                return operation.execute(primaryDeviceRepository);
            } else {
                primaryCache.add(operation);
            }
        } catch (Exception e) {
            primaryAvailable = false;
        }
        return operation.execute(backupDeviceRepository);
    }

    private interface RepositoryOperation<T> {
        T execute(DeviceRepository repo);
    }

    @Scheduled(cron = "10 * * * * *")
    private void connectionTest(){
        if(primaryAvailable) return;
        try {
            primaryDeviceRepository.count();
            syncBackupToPrimary();
            primaryAvailable = true;
        } catch(Exception ignored) {}
    }

    private void syncBackupToPrimary(){
        primaryCache.forEach(cached -> cached.execute(primaryDeviceRepository));
        primaryCache.clear();
    }
    
}
