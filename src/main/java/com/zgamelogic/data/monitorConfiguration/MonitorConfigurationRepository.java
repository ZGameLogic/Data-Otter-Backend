package com.zgamelogic.data.monitorConfiguration;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MonitorConfigurationRepository extends JpaRepository<MonitorConfiguration, Long> {
    List<MonitorConfiguration> findAllByActiveIsTrue();
}
