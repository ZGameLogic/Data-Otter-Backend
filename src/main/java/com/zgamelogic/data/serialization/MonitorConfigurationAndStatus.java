package com.zgamelogic.data.serialization;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zgamelogic.data.entities.MonitorConfiguration;
import com.zgamelogic.data.entities.MonitorStatus;
import com.zgamelogic.serialization.MonitorConfigStatusSerialization;


@JsonSerialize(using = MonitorConfigStatusSerialization.class)
public record MonitorConfigurationAndStatus(MonitorConfiguration monitorConfiguration, MonitorStatus monitorStatus) {}
