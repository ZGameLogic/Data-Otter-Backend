package com.zgamelogic.data.monitorHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface MonitorStatusRepository extends JpaRepository<MonitorStatus, MonitorStatusId> {
    @Query("SELECT ms from MonitorStatus ms where ms.id.monitor.id.monitorConfigurationId = :monitorId ORDER BY ms.id.date DESC")
    Optional<MonitorStatus> findTopStatusByMonitorId(long monitorId);

    @Query("SELECT ms FROM MonitorStatus ms WHERE ms.id.monitor.id.monitorConfigurationId = :monitorId AND ms.id.date BETWEEN :startDate AND :endDate ORDER BY ms.id.date DESC")
    List<MonitorStatus> findByMonitorIdAndDateBetween(@Param("monitorId") Long monitorId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Modifying
    @Transactional
    void deleteAllById_monitor_id_monitorConfigurationIdAndId_Monitor_Id_Application_Id(Long id_monitor_id_monitorConfigurationId, Long id_monitor_id_application_id);
}
