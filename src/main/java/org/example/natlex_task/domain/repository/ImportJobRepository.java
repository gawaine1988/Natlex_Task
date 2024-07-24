package org.example.natlex_task.domain.repository;

import org.example.natlex_task.domain.model.ImportJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ImportJobRepository extends JpaRepository<ImportJob, UUID> {
}
