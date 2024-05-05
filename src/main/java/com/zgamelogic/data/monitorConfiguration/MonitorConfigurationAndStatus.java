package com.zgamelogic.data.monitorConfiguration;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zgamelogic.data.monitorHistory.MonitorStatus;
import com.zgamelogic.serialization.MonitorConfigStatusSerialization;


@JsonSerialize(using = MonitorConfigStatusSerialization.class)
public record MonitorConfigurationAndStatus(MonitorConfiguration monitorConfiguration, MonitorStatus monitorStatus) {}
