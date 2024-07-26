package com.zgamelogic.data.monitorConfiguration;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MonitorConfigurationRepository extends JpaRepository<MonitorConfiguration, Long> {
    List<MonitorConfiguration> findAllByActiveIsTrue();
    Optional<MonitorConfiguration> findById_Id(Long id);
}
