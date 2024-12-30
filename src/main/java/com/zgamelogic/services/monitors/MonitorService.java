package com.zgamelogic.services.monitors;

import com.zgamelogic.data.monitorConfiguration.MonitorConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.Socket;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
public class MonitorService {

    @Async("asyncExecutor")
    public CompletableFuture<MonitorStatusReport> getMonitorStatus(MonitorConfiguration monitorConfiguration) {
        return switch(monitorConfiguration.getType()){
            case API, WEB -> getMonitorWebStatus(monitorConfiguration);
            case DATABASE -> getMonitorDatabaseStatus(monitorConfiguration);
        };
    }

    private CompletableFuture<MonitorStatusReport> getMonitorDatabaseStatus(MonitorConfiguration monitorConfiguration) {
        int attempts = 0;
        long startTime = 0;
        long endTime = 0;
        String[] splitUrl = monitorConfiguration.getUrl().split(":");
        String url = splitUrl[0];
        String portString = splitUrl.length > 1 ? monitorConfiguration.getUrl().split(":")[1] : "1433";
        int port = 1433;
        try {
            port = Integer.parseInt(portString);
        } catch (NumberFormatException ignored) {}
        while(attempts < 4) {
            startTime = System.currentTimeMillis();
            attempts++;
            try (Socket socket = new Socket(url, port)) {
                return CompletableFuture.completedFuture(new MonitorStatusReport(endTime - startTime, true, attempts, 0));
            } catch (IOException ignored) {}
        }
        return CompletableFuture.completedFuture(new MonitorStatusReport(endTime - startTime, false, attempts, 0));
    }

    private CompletableFuture<MonitorStatusReport> getMonitorWebStatus(MonitorConfiguration monitorConfiguration) {
        RestTemplate restTemplate =  new RestTemplateBuilder().setConnectTimeout(Duration.ofSeconds(2)).setReadTimeout(Duration.ofSeconds(2)).build();
        HttpHeaders headers = new HttpHeaders();
        int attempts = 0;
        ResponseEntity<String> response = null;
        long startTime = 0;
        long endTime = 0;
        while(attempts < 4){
            attempts++;
            startTime = System.currentTimeMillis();
            try {
                response = restTemplate.exchange(monitorConfiguration.getUrl(), HttpMethod.GET, null, String.class);
                endTime = System.currentTimeMillis();
                if(response.getStatusCode().is2xxSuccessful() && response.getBody().contains(monitorConfiguration.getRegex())){
                    return CompletableFuture.completedFuture(new MonitorStatusReport(endTime - startTime, true, attempts, response.getStatusCode().value()));
                }
            } catch (Exception ignored) {
                return CompletableFuture.completedFuture(new MonitorStatusReport(0, false, attempts, 0));
            }
        }
        return CompletableFuture.completedFuture(new MonitorStatusReport(endTime - startTime, false, attempts, response.getStatusCode().value()));
    }

    @Bean(name = "asyncExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(5000);
        executor.setThreadNamePrefix("Mon Serv-");
        executor.initialize();
        return executor;
    }
}
