package com.zgamelogic.data.websocket;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class WebsocketMessage {
    private final String type;
    private final Object message;
}
