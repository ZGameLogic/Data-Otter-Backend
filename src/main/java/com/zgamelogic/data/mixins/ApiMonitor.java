package com.zgamelogic.data.mixins;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zgamelogic.data.serializable.Status;

import java.util.Date;

public abstract class ApiMonitor {
    @JsonIgnore private long completedInMilliseconds;
    @JsonIgnore private Date taken;
    @JsonIgnore private Status status;
}
