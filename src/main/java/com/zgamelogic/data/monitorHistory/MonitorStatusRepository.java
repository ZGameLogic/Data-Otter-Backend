package com.zgamelogic.data.monitorHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface MonitorStatusRepository extends JpaRepository<MonitorStatus, MonitorStatus.MonitorStatusId> {
    Optional<MonitorStatus> findTopById_Monitor_Id_MonitorConfigurationIdAndId_Monitor_Id_Application_IdOrderById_Date(Long id_monitor_id_monitorConfigurationId, Long id_monitor_id_application_id);

    @Query("SELECT ms FROM MonitorStatus ms " +
            "WHERE ms.id.date = (SELECT MAX(subMs.id.date) FROM MonitorStatus subMs WHERE subMs.id.monitor.id = ms.id.monitor.id) " +
            "AND ms.id.monitor.id.application.id = :appId")
    List<MonitorStatus> findByApplicationIdAndTopOneForEachMonitor(Long appId);

    @Query("SELECT ms FROM MonitorStatus ms WHERE ms.id.monitor.id.monitorConfigurationId = :monitorId AND ms.id.monitor.id.application.id = :appId AND ms.id.date BETWEEN :startDate AND :endDate ORDER BY ms.id.date DESC")
    List<MonitorStatus> findByMonitorIdAndDateBetween(@Param("monitorId") Long monitorId, @Param("startDate") Date startDate, @Param("endDate") Date endDate, long appId);

    @Modifying
    @Transactional
    void deleteAllById_monitor_id_monitorConfigurationIdAndId_Monitor_Id_Application_Id(Long id_monitor_id_monitorConfigurationId, Long id_monitor_id_application_id);

    @Modifying
    @Transactional
    void deleteAllById_Monitor_Id_Application_Id(Long id_monitor_id_application_id);
}
