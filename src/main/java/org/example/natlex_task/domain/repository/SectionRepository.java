package org.example.natlex_task.domain.repository;

import org.example.natlex_task.domain.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SectionRepository extends JpaRepository<Section, UUID> {
    @Query("SELECT s FROM Section s JOIN s.geologicalClasses g WHERE g.code = :code")
    List<Section> findSectionsByGeologicalClassCode(@Param("code") String code);
}
