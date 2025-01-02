package com.zgamelogic.data.agentHistory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AgentStatusRepository extends JpaRepository<AgentStatus, AgentStatus.AgentStatusId> {
    Optional<AgentStatus> findTopById_Agent_IdOrderById_Date(long id);
}
