package com.zgamelogic.controllers;

import com.zgamelogic.data.components.DynamicApplicationRepository;
import com.zgamelogic.data.entities.Application;
import com.zgamelogic.data.serialization.ApplicationMonitorStatus;
import com.zgamelogic.data.entities.MonitorStatus;
import com.zgamelogic.data.repositories.MonitorStatusRepository;
import com.zgamelogic.data.repositories.NodeMonitorReportRepository;
import com.zgamelogic.data.repositories.TagRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("applications")
public class ApplicationController {

    private final DynamicApplicationRepository applicationRepository;
    private final MonitorStatusRepository monitorStatusRepository;
    private final NodeMonitorReportRepository nodeMonitorReportRepository;
    private final TagRepository tagRepository;

    public ApplicationController(DynamicApplicationRepository applicationRepository, MonitorStatusRepository monitorStatusRepository, NodeMonitorReportRepository nodeMonitorReportRepository, TagRepository tagRepository) {
        this.applicationRepository = applicationRepository;
        this.monitorStatusRepository = monitorStatusRepository;
        this.nodeMonitorReportRepository = nodeMonitorReportRepository;
        this.tagRepository = tagRepository;
    }

    @GetMapping
    public ResponseEntity<List<ApplicationMonitorStatus>> getApplications(@RequestParam(required = false, name = "include-status") Boolean includeStatus) {
        List<Application> apps = applicationRepository.findAll();
        List<ApplicationMonitorStatus> appMonitorStatuses = new ArrayList<>();
        for(Application app : apps) {
            List<MonitorStatus> statuses = null;
            if(includeStatus != null && includeStatus){
                statuses = monitorStatusRepository.findByApplicationIdAndTopOneForEachMonitor(app.getId());
            }
            appMonitorStatuses.add(new ApplicationMonitorStatus(app, statuses));
        }
        return ResponseEntity.ok(appMonitorStatuses);
    }

    @GetMapping("/{applicationId}")
    public ResponseEntity<ApplicationMonitorStatus> getApplication(
            @PathVariable long applicationId,
            @RequestParam(required = false, name = "include-status") Boolean includeStatus
    ) {
        Optional<Application> application = applicationRepository.findById(applicationId);
        if(application.isEmpty()) return ResponseEntity.notFound().build();
        List<MonitorStatus> statuses = null;
        if(includeStatus != null && includeStatus){
            statuses = monitorStatusRepository.findByApplicationIdAndTopOneForEachMonitor(application.get().getId());
        }
        ApplicationMonitorStatus returnObject = new ApplicationMonitorStatus(application.get(), statuses);
        return ResponseEntity.ok(returnObject);
    }

    @PostMapping
    public ResponseEntity<Application> createApplication(@RequestBody Application application){
        if(application.getName() == null) return ResponseEntity.badRequest().build();
        if(application.getTags() != null){
            application.getTags().stream()
                    .filter(tag -> !tagRepository.existsById(tag.getName()))
                    .forEach(tagRepository::save);
        }
        Application app = applicationRepository.save(application);
        return ResponseEntity.ok(app);
    }

    @PutMapping("/{applicationId}")
    public ResponseEntity<Application> updateApplication(@PathVariable long applicationId, @RequestBody Application application){
        Optional<Application> app = applicationRepository.findById(applicationId);
        if(app.isEmpty()) return ResponseEntity.notFound().build();
        app.get().update(application);
        Application updated = applicationRepository.save(app.get());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{applicationId}")
    public ResponseEntity<?> deleteApplication(@PathVariable long applicationId){
        if(!applicationRepository.existsById(applicationId)) return ResponseEntity.notFound().build();
        monitorStatusRepository.deleteAllById_Monitor_Id_Application_Id(applicationId);
        nodeMonitorReportRepository.deleteAllById_Monitor_Id_Application_Id(applicationId);
        applicationRepository.deleteById(applicationId);
        return ResponseEntity.ok().build();
    }
}
