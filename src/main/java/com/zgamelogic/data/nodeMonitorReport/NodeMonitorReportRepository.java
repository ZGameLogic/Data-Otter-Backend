package com.zgamelogic.data.nodeMonitorReport;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NodeMonitorReportRepository extends JpaRepository<NodeMonitorReport, NodeMonitorReportId> {
    @Modifying
    @Transactional
    @Query("DELETE FROM MonitorStatus where id.monitor.id = :monitorId")
    void deleteAllByMonitorId(long monitorId);

    List<NodeMonitorReport> findAllById_MonitorId(long monitorId);
}
