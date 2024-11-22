package com.zgamelogic.data.repositories;

import com.zgamelogic.data.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, String> {
}
