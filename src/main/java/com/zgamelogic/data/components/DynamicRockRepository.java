package com.zgamelogic.data.components;

import com.zgamelogic.data.entities.Rock;
import com.zgamelogic.data.repositories.RockRepository;
import com.zgamelogic.data.repositories.backup.BackupRockRepository;
import com.zgamelogic.data.repositories.primary.PrimaryRockRepository;
import com.zgamelogic.services.DatabaseConnectionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class DynamicRockRepository extends DynamicRepository<Rock, Rock.RockId, RockRepository> {

    protected DynamicRockRepository(PrimaryRockRepository primaryRepository, BackupRockRepository backupRepository, DatabaseConnectionService databaseConnectionService) {
        super(primaryRepository, backupRepository, databaseConnectionService);
    }

    public Page<Rock> findAllById_Application_IdOrderById_DateDesc(long appId, Pageable pageable) {
        return executeWithFallback(repo -> repo.findAllById_Application_IdOrderById_DateDesc(appId, pageable));
    }
}
