package com.zgamelogic.controllers;

import com.zgamelogic.data.application.ApplicationRepository;
import com.zgamelogic.data.metric.Metric;
import com.zgamelogic.data.metric.MetricRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("metrics")
@AllArgsConstructor
public class MetricController {
    private final MetricRepository metricRepository;
    private final ApplicationRepository applicationRepository;

    @PostMapping("/{appId}")
    private ResponseEntity<?> createMetric(@PathVariable long appId, @RequestBody Metric metric) {
        if(!applicationRepository.existsById(appId)) return ResponseEntity.notFound().build();
        Metric save = metricRepository.save(metric);
        return ResponseEntity.ok(save);
    }

    @GetMapping("/{appId}")
    private ResponseEntity<Page<Metric>> getMetrics(
        @PathVariable long appId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        if(!applicationRepository.existsById(appId)) return ResponseEntity.notFound().build();
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(metricRepository.findAllById_Application_IdOrderById_CollectedDesc(appId, pageable));
    }
}
