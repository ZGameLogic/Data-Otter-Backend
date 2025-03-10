package com.zgamelogic.data.metric;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MetricRepository extends JpaRepository<Metric, Metric.MetricId> {
    Page<Metric> findAllById_Application_IdOrderById_CollectedDesc(long appId, Pageable pageable);
}
