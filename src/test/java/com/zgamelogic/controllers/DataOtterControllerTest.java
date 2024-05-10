package com.zgamelogic.controllers;

import com.zgamelogic.data.monitorConfiguration.MonitorConfiguration;
import com.zgamelogic.data.monitorConfiguration.MonitorConfigurationRepository;
import com.zgamelogic.data.monitorHistory.MonitorStatusRepository;
import com.zgamelogic.data.nodeConfiguration.NodeConfiguration;
import com.zgamelogic.data.nodeConfiguration.NodeConfigurationRepository;
import com.zgamelogic.data.nodeMonitorReport.NodeMonitorReport;
import com.zgamelogic.data.nodeMonitorReport.NodeMonitorReportRepository;
import com.zgamelogic.services.monitors.MonitorService;
import com.zgamelogic.services.monitors.MonitorStatusReport;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.zgamelogic.data.Constants.MASTER_NODE_NAME;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class DataOtterControllerTest {

    @MockBean
    private MonitorConfigurationRepository monitorConfigurationRepository;
    @MockBean
    private MonitorStatusRepository monitorStatusRepository;
    @MockBean
    private NodeMonitorReportRepository nodeMonitorReportRepository;
    @MockBean
    private MonitorService monitorService;
    @MockBean
    private NodeConfigurationRepository nodeConfigurationRepository;
    @MockBean
    private NodeConfiguration masterNode;

    @BeforeEach
    void setUp() {
        MonitorConfiguration config = new MonitorConfiguration(1L, "test", MonitorConfiguration.Type.API, "https://zgamelogic.com/health", "Healthy");
        Mockito.when(monitorConfigurationRepository.findAll()).thenReturn(List.of(config));
        Mockito.when(monitorService.getMonitorStatus(Mockito.any())).thenReturn(CompletableFuture.completedFuture(new MonitorStatusReport(3, true, 1, 200)));
        masterNode = new NodeConfiguration(1L, MASTER_NODE_NAME);
    }

    @Test
    void testDataOtterTasks() {
        DataOtterController dataOtterController = new DataOtterController(monitorConfigurationRepository, monitorStatusRepository, nodeMonitorReportRepository, monitorService, masterNode);
        dataOtterController.dataOtterTasks();
        Mockito.verify(nodeMonitorReportRepository).save(Mockito.any(NodeMonitorReport.class));
    }

}
