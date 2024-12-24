package com.zgamelogic.data.application;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    @Query("SELECT a FROM Application a JOIN a.tags t WHERE t.name = :tagName")
    List<Application> findAllByTagName(@Param("tagName") String tagName);
}
