package com.zgamelogic.data;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class APIMonitor extends Monitor {

    private String url;
    private int port;
    private String healthCheckUrl;

}
