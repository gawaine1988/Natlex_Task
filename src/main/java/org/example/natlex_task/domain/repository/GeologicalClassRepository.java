package org.example.natlex_task.domain.repository;

import org.example.natlex_task.domain.model.GeologicalClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GeologicalClassRepository extends JpaRepository<GeologicalClass, UUID> {
}

