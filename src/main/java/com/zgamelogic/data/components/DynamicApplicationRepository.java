package com.zgamelogic.data.components;

import com.zgamelogic.data.entities.Application;
import com.zgamelogic.data.repositories.ApplicationRepository;
import com.zgamelogic.data.repositories.backup.BackupApplicationRepository;
import com.zgamelogic.data.repositories.primary.PrimaryApplicationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class DynamicApplicationRepository {
    private final PrimaryApplicationRepository primaryApplicationRepository;
    private final BackupApplicationRepository backupApplicationRepository;

    private boolean primaryAvailable;
    private final List<RepositoryOperation> primaryCache;

    public DynamicApplicationRepository(PrimaryApplicationRepository primaryApplicationRepository, BackupApplicationRepository backupApplicationRepository) {
        this.primaryApplicationRepository = primaryApplicationRepository;
        this.backupApplicationRepository = backupApplicationRepository;
        primaryAvailable = true;
        primaryCache = new ArrayList<>();
    }

    private <T> T executeWithFallback(RepositoryOperation<T> operation, boolean cache) {
        try {
            if (primaryAvailable) {
                return operation.execute(primaryApplicationRepository);
            } else {
                primaryCache.add(operation);
            }
        } catch (Exception e) {
            primaryAvailable = false;
        }
        return operation.execute(backupApplicationRepository);
    }

    public Application save(Application application) {
        backupApplicationRepository.save(application);
        return executeWithFallback(repo -> repo.save(application), true);
    }

    public void deleteById(long applicationId) {
        try {
            if (primaryAvailable) {
                primaryApplicationRepository.deleteById(applicationId);
            }
        } catch (Exception e) {
            primaryAvailable = false;
        }
        backupApplicationRepository.deleteById(applicationId);
    }

    public List<Application> findAll() {
        return executeWithFallback(ApplicationRepository::findAll, false);
    }

    public Optional<Application> findById(long applicationId) {
        return executeWithFallback(repo -> repo.findById(applicationId), false);
    }

    public boolean existsById(long applicationId) {
        return executeWithFallback(repo -> repo.existsById(applicationId), false);
    }

    public List<Application> findAllByTagName(String tagId) {
        return executeWithFallback(repo -> repo.findAllByTagName(tagId), false);
    }

    public Application getReferenceById(long applicationId) {
        return executeWithFallback(repo -> repo.getReferenceById(applicationId), false);
    }

    private interface RepositoryOperation<T> {
        T execute(ApplicationRepository repo);
    }

    @Scheduled(cron = "10 * * * * *")
    private void connectionTest(){
        if(primaryAvailable) return;
        try {
            primaryApplicationRepository.count();
            syncBackupToPrimary();
            primaryAvailable = true;
        } catch(Exception ignored) {}
    }

    private void syncBackupToPrimary(){
        primaryCache.forEach(cached -> cached.execute(primaryApplicationRepository));
        primaryCache.clear();
    }
}
