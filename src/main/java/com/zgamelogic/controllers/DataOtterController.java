package com.zgamelogic.controllers;

import com.zgamelogic.data.monitorConfiguration.MonitorConfigurationRepository;
import com.zgamelogic.data.monitorHistory.MonitorStatusRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class DataOtterController {
    private final MonitorConfigurationRepository monitorConfigurationRepository;
    private final MonitorStatusRepository monitorStatusRepository;

    public DataOtterController(MonitorConfigurationRepository monitorConfigurationRepository, MonitorStatusRepository monitorStatusRepository) {
        this.monitorConfigurationRepository = monitorConfigurationRepository;
        this.monitorStatusRepository = monitorStatusRepository;
    }
}
