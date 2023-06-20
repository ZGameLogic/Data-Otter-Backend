package com.zgamelogic.data;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class APIMonitor extends Monitor {

    private String url;
    private int port;
    private String healthCheckUrl;

    public APIMonitor(String name, String url, int port, String healthCheckUrl) {
        this.url = url;
        this.port = port;
        this.healthCheckUrl = healthCheckUrl;
        setName(name);
        setType("api");
    }
}
