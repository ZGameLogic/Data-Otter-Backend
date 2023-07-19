package com.zgamelogic.data.mixins;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

public abstract class ApiMonitor {
    @JsonIgnore private long completedInMilliseconds;
    @JsonIgnore private boolean status;
    @JsonIgnore private Date taken;
}
