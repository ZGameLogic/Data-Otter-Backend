package com.zgamelogic.services;

import com.zgamelogic.data.events.DatabaseConnectionEvent;
import com.zgamelogic.data.repositories.primary.PrimaryApplicationRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DatabaseConnectionService {
    @Getter
    @Setter
    private boolean databaseConnected;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final PrimaryApplicationRepository database;

    public DatabaseConnectionService(ApplicationEventPublisher applicationEventPublisher, PrimaryApplicationRepository database) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.database = database;
        databaseConnected = true;
    }

    @Scheduled(cron = "10 * * * * *")
    private void connectionTest(){
        try {
            database.count();
            if(!databaseConnected) {
                log.info("Database connection restored.");
                applicationEventPublisher.publishEvent(new DatabaseConnectionEvent(this, true));
                databaseConnected = true;
            }
        } catch (Exception e){
            log.info("Database connection lost.");
            applicationEventPublisher.publishEvent(new DatabaseConnectionEvent(this, false));
            databaseConnected = false;
        }
    }
}
