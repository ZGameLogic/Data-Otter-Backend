package com.zgamelogic.configurations;

import com.zgamelogic.services.DataOtterWebsocketService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebsocketConfiguration implements WebSocketConfigurer {
    private final DataOtterWebsocketService dataOtterWebsocketService;

    public WebsocketConfiguration(DataOtterWebsocketService dataOtterWebsocketService) {
        this.dataOtterWebsocketService = dataOtterWebsocketService;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(dataOtterWebsocketService, "/live").setAllowedOrigins("*");
    }
}
