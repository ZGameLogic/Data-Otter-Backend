package com.zgamelogic.data.serializable;

import com.zgamelogic.data.serializable.Monitor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WebMonitor extends Monitor {

    private String url;
    private int port;
    private String regex;

    public WebMonitor(String name, String url, int port, String regex) {
        this.url = url;
        this.port = port;
        this.regex = regex;
        setName(name);
        setType("web");
    }
}
