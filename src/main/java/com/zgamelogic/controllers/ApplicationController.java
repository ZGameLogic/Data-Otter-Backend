package com.zgamelogic.controllers;

import com.zgamelogic.data.application.Application;
import com.zgamelogic.data.application.ApplicationMonitorStatus;
import com.zgamelogic.data.application.ApplicationRepository;
import com.zgamelogic.data.monitorHistory.MonitorStatus;
import com.zgamelogic.data.monitorHistory.MonitorStatusRepository;
import com.zgamelogic.data.tags.TagRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("applications")
public class ApplicationController {

    private final ApplicationRepository applicationRepository;
    private final MonitorStatusRepository monitorStatusRepository;
    private final TagRepository tagRepository;

    public ApplicationController(ApplicationRepository applicationRepository, MonitorStatusRepository monitorStatusRepository, TagRepository tagRepository) {
        this.applicationRepository = applicationRepository;
        this.monitorStatusRepository = monitorStatusRepository;
        this.tagRepository = tagRepository;
    }

    @GetMapping
    public ResponseEntity<List<Application>> getApplications() {
        return ResponseEntity.ok(applicationRepository.findAll());
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
        applicationRepository.deleteById(applicationId);
        return ResponseEntity.ok().build();
    }

}
