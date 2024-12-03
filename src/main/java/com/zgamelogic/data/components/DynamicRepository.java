package com.zgamelogic.data.components;

import com.zgamelogic.data.events.DatabaseConnectionEvent;
import com.zgamelogic.services.DatabaseConnectionService;
import lombok.Getter;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class DynamicRepository<T, ID, R extends JpaRepository<T, ID>> {
    private final R primaryRepository;
    private final R backupRepository;
    private final DatabaseConnectionService databaseConnectionService;
    private final List<RepositoryOperation> primaryCache;
    private final List<VoidRepositoryOperation> primaryVoidCache;

    protected DynamicRepository(R primaryRepository, R backupRepository, DatabaseConnectionService databaseConnectionService) {
        this.primaryRepository = primaryRepository;
        this.backupRepository = backupRepository;
        this.databaseConnectionService = databaseConnectionService;
        this.primaryCache = new ArrayList<>();
        this.primaryVoidCache = new ArrayList<>();
        syncPrimaryToBackup();
    }

    public T save(T entity) {
        try {
            T saved = primaryRepository.save(entity);
            backupRepository.save(saved);
            return saved;
        } catch(Exception e){
            T saved = backupRepository.save(entity);
            primaryCache.add(repo -> repo.save(saved));
            return saved;
        }
    }

    protected void executeOnBothVoid(VoidRepositoryOperation<R> operation) {
        executeOnBothVoid(operation, false);
    }

    protected void executeOnBothVoid(VoidRepositoryOperation<R> operation, boolean cache) {
        if(!databaseConnectionService.isDatabaseConnected()){
            if(cache) primaryVoidCache.add(operation);
            operation.execute(backupRepository);
        } else {
            try {
                operation.execute(primaryRepository);
            } catch (Exception e) {
                databaseConnectionService.setDatabaseConnected(false);
                if(cache) primaryVoidCache.add(operation);
            }
            operation.execute(backupRepository);
        }
    }

    protected void executeWithFallbackVoid(VoidRepositoryOperation<R> operation) {
        executeWithFallbackVoid(operation, false);
    }

    protected void executeWithFallbackVoid(VoidRepositoryOperation<R> operation, boolean cache) {
        try {
            if(databaseConnectionService.isDatabaseConnected()){
                operation.execute(primaryRepository);
            } else if(cache){
                primaryVoidCache.add(operation);
            }
        } catch (Exception e) {
            databaseConnectionService.setDatabaseConnected(false);
            if(cache) primaryVoidCache.add(operation);
        }
        operation.execute(backupRepository);
    }

    protected <U> U executeOnBoth(RepositoryOperation<R, U> operation) {
        return executeOnBoth(operation, false);
    }

    protected <U> U executeOnBoth(RepositoryOperation<R, U> operation, boolean cache) {
        if(!databaseConnectionService.isDatabaseConnected()){
            if(cache) primaryCache.add(operation);
            return operation.execute(backupRepository);
        } else {
            U result = null;
            try {
                result =  operation.execute(primaryRepository);
            } catch (Exception e) {
                databaseConnectionService.setDatabaseConnected(false);
                if(cache) primaryCache.add(operation);
            }
            if(result == null){
                result = operation.execute(backupRepository);
            } else {
                operation.execute(backupRepository);
            }
            return result;
        }
    }

    protected <U> U executeWithFallback(RepositoryOperation<R, U> operation) {
        return executeWithFallback(operation, false);
    }

    protected <U> U executeWithFallback(RepositoryOperation<R, U> operation, boolean cache) {
        try {
            if (databaseConnectionService.isDatabaseConnected()) {
                return operation.execute(primaryRepository);
            } else if (cache) {
                primaryCache.add(operation);
            }
        } catch (Exception e) {
            databaseConnectionService.setDatabaseConnected(false);
            if(cache) primaryCache.add(operation);
        }
        return operation.execute(backupRepository);
    }

    @EventListener
    private void connectionEvent(DatabaseConnectionEvent event) {
        if (event.isConnected()) {
            syncBackupToPrimary();
        }
    }

    private void syncBackupToPrimary() {
        for (RepositoryOperation<R, ?> operation : primaryCache) {
            operation.execute(primaryRepository);
        }
        for (VoidRepositoryOperation<R> operation : primaryVoidCache) {
            operation.execute(primaryRepository);
        }
        primaryVoidCache.clear();
        primaryCache.clear();
    }

    protected void syncPrimaryToBackup() {
        backupRepository.deleteAll();
        backupRepository.saveAll(primaryRepository.findAll());
    }

    protected interface RepositoryOperation<R extends JpaRepository, U> {
        U execute(R repository);
    }

    protected interface VoidRepositoryOperation<R> {
        void execute(R repository);
    }
}