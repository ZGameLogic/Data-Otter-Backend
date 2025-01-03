package com.zgamelogic.controllers;

import com.zgamelogic.data.application.Application;
import com.zgamelogic.data.application.ApplicationMonitorStatus;
import com.zgamelogic.data.application.ApplicationRepository;
import com.zgamelogic.data.monitorHistory.MonitorStatus;
import com.zgamelogic.data.monitorHistory.MonitorStatusRepository;
import com.zgamelogic.data.nodeMonitorReport.NodeMonitorReportRepository;
import com.zgamelogic.data.tags.TagRepository;
import com.zgamelogic.services.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("applications")
public class ApplicationController {

    private final ApplicationRepository applicationRepository;
    private final MonitorStatusRepository monitorStatusRepository;
    private final NodeMonitorReportRepository nodeMonitorReportRepository;
    private final CacheService cacheService;
    private final TagRepository tagRepository;

    public ApplicationController(ApplicationRepository applicationRepository, MonitorStatusRepository monitorStatusRepository, NodeMonitorReportRepository nodeMonitorReportRepository, CacheService cacheService, TagRepository tagRepository) {
        this.applicationRepository = applicationRepository;
        this.monitorStatusRepository = monitorStatusRepository;
        this.nodeMonitorReportRepository = nodeMonitorReportRepository;
        this.cacheService = cacheService;
        this.tagRepository = tagRepository;
    }

    @GetMapping
    public ResponseEntity<List<ApplicationMonitorStatus>> getApplications(@RequestParam(required = false, name = "include-status") Boolean includeStatus) {
        List<ApplicationMonitorStatus> statuses = cacheService.getAppMonitorStatuses();
        if(!includeStatus) {
            statuses = statuses.stream().map(
                    status -> new ApplicationMonitorStatus(status.application(), null)
            ).toList();
        }
        return ResponseEntity.ok(statuses);
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
