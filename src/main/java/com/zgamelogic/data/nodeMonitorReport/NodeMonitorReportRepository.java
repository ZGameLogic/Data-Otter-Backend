package com.zgamelogic.data.nodeMonitorReport;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NodeMonitorReportRepository extends JpaRepository<NodeMonitorReport, NodeMonitorReportId> {
    @Modifying
    @Transactional
    void deleteAllById_monitor_id_monitorConfigurationId(long monitorId);

    List<NodeMonitorReport> findAllById_Monitor_Id_MonitorConfigurationId(long monitorId);
}
