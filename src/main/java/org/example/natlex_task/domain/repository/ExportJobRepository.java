package org.example.natlex_task.domain.repository;

import org.example.natlex_task.domain.model.ExportJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ExportJobRepository extends JpaRepository<ExportJob, UUID> {
}
