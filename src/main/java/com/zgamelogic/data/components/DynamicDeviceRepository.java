package com.zgamelogic.data.components;

import com.zgamelogic.data.entities.Device;
import com.zgamelogic.data.repositories.DeviceRepository;
import com.zgamelogic.data.repositories.backup.BackupDeviceRepository;
import com.zgamelogic.data.repositories.primary.PrimaryDeviceRepository;
import com.zgamelogic.services.DatabaseConnectionService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DynamicDeviceRepository extends DynamicRepository<Device, String, DeviceRepository> {
    protected DynamicDeviceRepository(PrimaryDeviceRepository primaryRepository, BackupDeviceRepository backupRepository, DatabaseConnectionService databaseConnectionService) {
        super(primaryRepository, backupRepository, databaseConnectionService);
    }

    public List<Device> findAll() {
        return executeWithFallback(DeviceRepository::findAll, false);
    }

    public void deleteById(String deviceId) {
        executeWithFallbackVoid(repo -> repo.deleteById(deviceId));
    }
}
