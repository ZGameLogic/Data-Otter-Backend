package com.zgamelogic.data.monitorHistory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MonitorStatusRepository extends JpaRepository<MonitorStatus, MonitorStatusId> {
    Optional<MonitorStatus> findTop1ById_MonitorIdOrderById_DateDesc(long monitorId);
}
