package com.zgamelogic.controllers;

import com.zgamelogic.data.monitorConfiguration.MonitorConfiguration;
import com.zgamelogic.data.monitorConfiguration.MonitorConfigurationRepository;
import com.zgamelogic.data.monitorHistory.MonitorStatus;
import com.zgamelogic.data.monitorHistory.MonitorStatusRepository;
import com.zgamelogic.data.nodeConfiguration.NodeConfiguration;
import com.zgamelogic.data.nodeConfiguration.NodeConfigurationRepository;
import com.zgamelogic.data.nodeMonitorReport.NodeMonitorReport;
import com.zgamelogic.data.nodeMonitorReport.NodeMonitorReportRepository;
import com.zgamelogic.services.monitors.MonitorService;
import com.zgamelogic.services.monitors.MonitorStatusReport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.zgamelogic.data.Constants.MASTER_NODE_NAME;

@SpringBootTest
@AutoConfigureMockMvc
class DataOtterControllerTest {

    @MockBean
    private static MonitorConfigurationRepository monitorConfigurationRepository;
    @MockBean
    private static MonitorStatusRepository monitorStatusRepository;
    @MockBean
    private static NodeMonitorReportRepository nodeMonitorReportRepository;
    @MockBean
    private static MonitorService monitorService;
    @MockBean
    private static NodeConfigurationRepository nodeConfigurationRepository;

    @Autowired
    private DataOtterController dataOtterController;

    @BeforeAll
    static void setUp() {
        MonitorConfiguration config = new MonitorConfiguration(1L, "test", MonitorConfiguration.Type.API, "https://zgamelogic.com/health", "Healthy");
        Mockito.when(monitorConfigurationRepository.findAll()).thenReturn(List.of(config));
        Mockito.when(monitorService.getMonitorStatus(Mockito.any())).thenReturn(CompletableFuture.completedFuture(new MonitorStatusReport(3, true, 1, 200)));
        Mockito.when(nodeConfigurationRepository.findByName(MASTER_NODE_NAME)).thenReturn(Optional.of(new NodeConfiguration(1L, MASTER_NODE_NAME)));
    }

    @Test
    void testDataOtterTasks() {
        dataOtterController.dataOtterTasks();
        Mockito.verify(nodeMonitorReportRepository).save(Mockito.any(NodeMonitorReport.class));
    }

}
