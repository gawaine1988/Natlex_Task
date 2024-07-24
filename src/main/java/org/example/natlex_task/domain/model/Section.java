package org.example.natlex_task.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.natlex_task.domain.model.GeologicalClass;
import org.hibernate.annotations.JdbcTypeCode;

import java.util.List;
import java.util.UUID;

import static java.sql.Types.VARCHAR;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sections")
@EqualsAndHashCode
public class Section {
    @Id
    @Column(name = "section_id", updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
    @JdbcTypeCode(value = VARCHAR)
    private UUID sectionId;

    private String name;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private ImportJob importJob;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GeologicalClass> geologicalClasses;
}
