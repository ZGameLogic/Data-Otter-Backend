package com.zgamelogic.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class DataOtterWebsocketService extends TextWebSocketHandler {
    private final List<WebSocketSession> sessions;

    public DataOtterWebsocketService() {
        sessions = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.debug("Connected: {}", session.getId());
        sessions.add(session);
    }

    @Override
    protected void handleTextMessage(@NotNull WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        log.debug("Received: {}", payload);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, @NotNull CloseStatus status) {
        log.debug("Disconnected: {}", session.getId());
        sessions.remove(session);
    }

    public void sendMessage(Object message){
        ObjectMapper om = new ObjectMapper();
        sessions.forEach(session -> {
            try {
                session.sendMessage(new BinaryMessage(om.writeValueAsBytes(message)));
            } catch (IOException e) {
                log.error("Error sending message", e);
            }
        });
    }
}
