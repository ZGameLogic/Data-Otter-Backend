package com.zgamelogic.data.rock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RockRepository extends JpaRepository<Rock, Rock.RockId> {
    Page<Rock> findAllById_Application_IdOrderById_DateDesc(Long appId, Pageable pageable);
    long countAllById_Application_Id(Long appId);
}
