package com.zgamelogic.data.mixins;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class ApiMonitor {
    @JsonIgnore private long completedInMilliseconds;
    @JsonIgnore private boolean status;
}
