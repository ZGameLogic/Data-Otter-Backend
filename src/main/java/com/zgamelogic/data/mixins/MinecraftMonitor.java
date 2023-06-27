package com.zgamelogic.data.mixins;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public abstract class MinecraftMonitor {

    @JsonIgnore private int max;
    @JsonIgnore private int online;
    @JsonIgnore private List<String> onlinePlayers;
    @JsonIgnore private String version;
    @JsonIgnore private String motd;
    @JsonIgnore private long completedInMilliseconds;
    @JsonIgnore private boolean status;
}
