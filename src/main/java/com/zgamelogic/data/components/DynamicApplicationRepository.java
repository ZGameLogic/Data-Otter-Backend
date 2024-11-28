package com.zgamelogic.data.components;

import com.zgamelogic.data.entities.Application;
import com.zgamelogic.data.repositories.ApplicationRepository;
import com.zgamelogic.data.repositories.backup.BackupApplicationRepository;
import com.zgamelogic.data.repositories.primary.PrimaryApplicationRepository;
import com.zgamelogic.services.DatabaseConnectionService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class DynamicApplicationRepository extends DynamicRepository<Application, Long, ApplicationRepository> {
    protected DynamicApplicationRepository(PrimaryApplicationRepository primaryRepository, BackupApplicationRepository backupRepository, DatabaseConnectionService databaseConnectionService) {
        super(primaryRepository, backupRepository, databaseConnectionService);
    }

    public Application save(Application application) {
        getBackupRepository().save(application);
        return executeWithFallback(repo -> repo.save(application), true);
    }

    public void deleteById(long applicationId) {
        executeWithFallbackVoid(repo -> repo.deleteById(applicationId), true);
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
}
