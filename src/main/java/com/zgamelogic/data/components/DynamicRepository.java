package com.zgamelogic.data.components;

import com.zgamelogic.data.events.DatabaseConnectionEvent;
import com.zgamelogic.services.DatabaseConnectionService;
import lombok.Getter;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class DynamicRepository {
    private final JpaRepository primaryRepository;
    private final JpaRepository backupRepository;
    private final DatabaseConnectionService databaseConnectionService;

    private final List<RepositoryOperation> primaryCache;

    public DynamicRepository(JpaRepository primaryRepository, JpaRepository backupRepository, DatabaseConnectionService databaseConnectionService) {
        this.primaryRepository = primaryRepository;
        this.backupRepository = backupRepository;
        this.databaseConnectionService = databaseConnectionService;
        primaryCache = new ArrayList<>();
    }

    protected <T> T executeWithFallback(RepositoryOperation<T> operation, boolean cache) {
        try {
            if (databaseConnectionService.isDatabaseConnected()) {
                return operation.execute(primaryRepository);
            } else {
                primaryCache.add(operation);
            }
        } catch (Exception e) {
            databaseConnectionService.setDatabaseConnected(false);
        }
        return operation.execute(backupRepository);
    }

    protected interface RepositoryOperation<T> {
        T execute(JpaRepository repo);
    }

    protected abstract void syncBackupToPrimary();

    @EventListener
    private void connectionEvent(DatabaseConnectionEvent event){
        if(event.isConnected()) syncBackupToPrimary();
    }
}
