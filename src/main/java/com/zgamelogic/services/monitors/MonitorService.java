package com.zgamelogic.services.monitors;

import com.zgamelogic.data.monitorConfiguration.MonitorConfiguration;
import org.springframework.stereotype.Service;

@Service
public class MonitorService {
    public MonitorStatusReport getMonitorStatus(MonitorConfiguration monitorConfiguration) {
        // TODO actually get the monitor status
        return new MonitorStatusReport(23, true, 1, 200);
    }
}
