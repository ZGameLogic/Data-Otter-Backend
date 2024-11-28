package com.zgamelogic.data.components;

import com.zgamelogic.data.entities.Tag;
import com.zgamelogic.data.repositories.TagRepository;
import com.zgamelogic.data.repositories.backup.BackupTagRepository;
import com.zgamelogic.data.repositories.primary.PrimaryTagRepository;
import com.zgamelogic.services.DatabaseConnectionService;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DynamicTagRepository extends DynamicRepository<Tag, String, TagRepository> {
    protected DynamicTagRepository(PrimaryTagRepository primaryRepository, BackupTagRepository backupRepository, DatabaseConnectionService databaseConnectionService) {
        super(primaryRepository, backupRepository, databaseConnectionService);
    }

    public boolean existsById(String name) {
        return executeWithFallback(repo -> repo.existsById(name));
    }

    public List<Tag> findAll() {
        return executeWithFallback(ListCrudRepository::findAll);
    }

    public void deleteById(String tagId) {
        executeOnBothVoid(repo -> repo.deleteById(tagId));
    }

    public Tag getReferenceById(String tagId) {
        return executeWithFallback(repo -> repo.getReferenceById(tagId));
    }
}
