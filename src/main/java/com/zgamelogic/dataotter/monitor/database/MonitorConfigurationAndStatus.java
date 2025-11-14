package com.zgamelogic.dataotter.monitor.database;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zgamelogic.dataotter.monitor.serialization.MonitorConfigStatusSerialization;


@JsonSerialize(using = MonitorConfigStatusSerialization.class)
public record MonitorConfigurationAndStatus(MonitorConfiguration monitorConfiguration, MonitorStatus monitorStatus) {}
