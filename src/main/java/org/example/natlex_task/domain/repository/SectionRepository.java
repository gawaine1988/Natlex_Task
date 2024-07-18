package org.example.natlex_task.domain.repository;

import org.example.natlex_task.domain.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SectionRepository extends JpaRepository<Section, UUID> {
}
