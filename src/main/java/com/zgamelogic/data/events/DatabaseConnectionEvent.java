package com.zgamelogic.data.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class DatabaseConnectionEvent extends ApplicationEvent {
    private final boolean connected;

    public DatabaseConnectionEvent(Object source, boolean connected) {
        super(source);
        this.connected = connected;
    }
}
