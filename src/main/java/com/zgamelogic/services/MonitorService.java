package com.zgamelogic.services;

import com.zgamelogic.data.monitorConfiguration.MonitorConfiguration;
import com.zgamelogic.data.monitorHistory.MonitorStatus;
import org.springframework.stereotype.Service;

@Service
public class MonitorService {
    public MonitorStatus getMonitorStatus(MonitorConfiguration monitorConfiguration) {
        // TODO actually get the monitor status
        return new MonitorStatus(monitorConfiguration.getId(), 32, true, 1, 200);
    }
}
