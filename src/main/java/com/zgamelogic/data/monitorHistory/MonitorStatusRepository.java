package com.zgamelogic.data.monitorHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface MonitorStatusRepository extends JpaRepository<MonitorStatus, MonitorStatusId> {
    Optional<MonitorStatus> findTop1ById_MonitorIdOrderById_DateDesc(long monitorId);

    @Query("SELECT ms FROM MonitorStatus ms WHERE ms.id.monitor.id = :monitorId AND ms.id.date BETWEEN :startDate AND :endDate")
    List<MonitorStatus> findByMonitorIdAndDateBetween(@Param("monitorId") Long monitorId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
