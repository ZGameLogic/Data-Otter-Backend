package com.zgamelogic.services;

import com.zgamelogic.data.events.DatabaseConnectionEvent;
import com.zgamelogic.data.repositories.primary.PrimaryApplicationRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DatabaseConnectionService {
    @Getter
    private boolean databaseConnected;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final PrimaryApplicationRepository database;

    public DatabaseConnectionService(ApplicationEventPublisher applicationEventPublisher, PrimaryApplicationRepository database) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.database = database;
        databaseConnected = true;
    }

    public void setDatabaseConnected(boolean databaseConnected) {
        if(this.databaseConnected && !databaseConnected) {
            log.warn("Database connection lost.");
        } else if(!this.databaseConnected && databaseConnected) {
            log.info("Database connection restored.");
        }
        applicationEventPublisher.publishEvent(new DatabaseConnectionEvent(this, databaseConnected));
        this.databaseConnected = databaseConnected;
    }

    @Scheduled(cron = "10 * * * * *")
    private void connectionTest(){
        try {
            database.count();
            if(!databaseConnected) {
                setDatabaseConnected(true);
            }
        } catch (Exception e){
            setDatabaseConnected(false);
        }
    }
}
