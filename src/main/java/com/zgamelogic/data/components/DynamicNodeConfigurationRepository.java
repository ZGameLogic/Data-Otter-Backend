package com.zgamelogic.data.components;

import com.zgamelogic.data.entities.NodeConfiguration;
import com.zgamelogic.data.repositories.NodeConfigurationRepository;
import com.zgamelogic.data.repositories.backup.BackupNodeConfigurationRepository;
import com.zgamelogic.data.repositories.primary.PrimaryNodeConfigurationRepository;
import com.zgamelogic.services.DatabaseConnectionService;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DynamicNodeConfigurationRepository extends DynamicRepository<NodeConfiguration, Long, NodeConfigurationRepository>{
    public DynamicNodeConfigurationRepository(PrimaryNodeConfigurationRepository primaryRepository, BackupNodeConfigurationRepository backupRepository, DatabaseConnectionService databaseConnectionService) {
        super(primaryRepository, backupRepository, databaseConnectionService);
    }

    public boolean existsById(long nodeId) {
        return executeWithFallback(repo -> repo.existsById(nodeId));
    }

    public NodeConfiguration save(NodeConfiguration nodeConfiguration) {
        return executeWithFallback(repo -> repo.save(nodeConfiguration), true);
    }

    public Optional<NodeConfiguration> findByName(String masterNodeName) {
        return executeWithFallback(repo -> repo.findByName(masterNodeName));
    }
}
