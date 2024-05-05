package com.zgamelogic.services.monitors;

import com.zgamelogic.data.monitorConfiguration.MonitorConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
public class MonitorService {

    @Async("asyncExecutor")
    public CompletableFuture<MonitorStatusReport> getMonitorStatus(MonitorConfiguration monitorConfiguration) {
        // TODO actually get the monitor status
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return CompletableFuture.completedFuture(new MonitorStatusReport(23, true, 1, 200));
    }

    @Bean(name = "asyncExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("Monitor Service-");
        executor.initialize();
        return executor;
    }
}
