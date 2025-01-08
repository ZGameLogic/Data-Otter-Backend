package com.zgamelogic.data.agentHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface AgentStatusRepository extends JpaRepository<AgentStatus, AgentStatus.AgentStatusId> {
    Optional<AgentStatus> findFirstByIdAgentIdAndIdDateAfterOrderByIdDateDesc(long id, Date date);

    @Query("SELECT a FROM AgentStatus a WHERE a.id.agent.id = :agentId AND a.id.date BETWEEN :startDate AND :endDate ORDER BY a.id.date DESC")
    List<AgentStatus> findByAgentIdAndDateBetween(long agentId, Date startDate, Date endDate);

    @Modifying
    @Transactional
    @Query("DELETE FROM AgentStatus ms WHERE ms.id.date < :cutoffDate")
    void deleteRecordsOlderThan(Date cutoffDate);
}
