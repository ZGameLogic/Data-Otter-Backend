package com.zgamelogic.data.mixins;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zgamelogic.data.serializable.Status;

import java.util.Date;
import java.util.List;

public abstract class MinecraftMonitor {

    @JsonIgnore private int max;
    @JsonIgnore private int online;
    @JsonIgnore private List<String> onlinePlayers;
    @JsonIgnore private String version;
    @JsonIgnore private String motd;
    @JsonIgnore private long completedInMilliseconds;
    @JsonIgnore private Date taken;
    @JsonIgnore private Status status;
}
