package com.zgamelogic.data.repositories;

import com.zgamelogic.data.entities.NodeConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NodeConfigurationRepository extends JpaRepository<NodeConfiguration, Long> {
    Optional<NodeConfiguration> findByName(String name);
}
