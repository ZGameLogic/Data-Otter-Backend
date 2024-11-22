package com.zgamelogic.data.repositories;

import com.zgamelogic.data.entities.MonitorConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface MonitorConfigurationRepository extends JpaRepository<MonitorConfiguration, Long> {
    List<MonitorConfiguration> findAllByActiveIsTrue();
    Optional<MonitorConfiguration> findById_MonitorConfigurationIdAndId_Application_Id(Long id, Long applicationId);
    boolean existsById_MonitorConfigurationIdAndId_Application_Id(Long id, Long applicationId);

    @Transactional
    void deleteById_MonitorConfigurationId(Long id);
}
