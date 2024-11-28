package com.zgamelogic.data.components;

import com.zgamelogic.data.entities.NodeMonitorReport;
import com.zgamelogic.data.repositories.NodeMonitorReportRepository;
import com.zgamelogic.data.repositories.backup.BackupNodeMonitorReportRepository;
import com.zgamelogic.data.repositories.primary.PrimaryNodeMonitorReportRepository;
import com.zgamelogic.services.DatabaseConnectionService;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DynamicNodeMonitorReportRepository extends DynamicRepository<NodeMonitorReport, NodeMonitorReport.NodeMonitorReportId, NodeMonitorReportRepository> {
    protected DynamicNodeMonitorReportRepository(PrimaryNodeMonitorReportRepository primaryRepository, BackupNodeMonitorReportRepository backupRepository, DatabaseConnectionService databaseConnectionService) {
        super(primaryRepository, backupRepository, databaseConnectionService);
    }

    public void deleteAllById_Monitor_Id_Application_Id(long applicationId) {
        executeOnBothVoid(repo -> repo.deleteAllById_Monitor_Id_Application_Id(applicationId), true);
    }

    public void deleteAllById_monitor_id_monitorConfigurationId(long id) {
        executeOnBothVoid(repo -> repo.deleteAllById_monitor_id_monitorConfigurationId(id), true);
    }

    public void deleteAll() {
        executeOnBothVoid(CrudRepository::deleteAll, true);
    }

    public NodeMonitorReport save(NodeMonitorReport nodeMonitorReport) {
        return executeOnBoth(repo -> repo.save(nodeMonitorReport), true);
    }

    public List<NodeMonitorReport> findAllById_Monitor_Id_MonitorConfigurationId(Long monitorConfigurationId) {
        return executeWithFallback(repo -> repo.findAllById_Monitor_Id_MonitorConfigurationId(monitorConfigurationId));
    }
}
