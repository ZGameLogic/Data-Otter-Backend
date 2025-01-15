package com.zgamelogic.data.nodeMonitorReport;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NodeMonitorReportRepository extends JpaRepository<NodeMonitorReport, NodeMonitorReport.NodeMonitorReportId> {
    @Modifying
    @Transactional
    void deleteAllById_monitor_id_monitorConfigurationId(long monitorId);
    @Modifying
    @Transactional
    void deleteAllById_Monitor_Id_Application_Id(long applicationId);

    List<NodeMonitorReport> findAllById_Monitor_Id_MonitorConfigurationId(long monitorId);
    List<NodeMonitorReport> findAllById_Monitor_Id_Application_IdAndId_Monitor_Id_MonitorConfigurationId(long appId, long monitorId);
}
