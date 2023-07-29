package com.zgamelogic.data.serializable.monitors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
public class MinecraftMonitor extends Monitor {

    private String url;
    private int port;

}
