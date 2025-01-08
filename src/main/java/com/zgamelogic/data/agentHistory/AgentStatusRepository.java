package com.zgamelogic.data.agentHistory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface AgentStatusRepository extends JpaRepository<AgentStatus, AgentStatus.AgentStatusId> {
    Optional<AgentStatus> findFirstByIdAgentIdAndIdDateAfterOrderByIdDateDesc(long id, Date date);
}
