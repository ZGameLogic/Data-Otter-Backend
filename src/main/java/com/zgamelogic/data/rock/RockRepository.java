package com.zgamelogic.data.rock;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RockRepository extends JpaRepository<Rock, Rock.RockId> {
}
