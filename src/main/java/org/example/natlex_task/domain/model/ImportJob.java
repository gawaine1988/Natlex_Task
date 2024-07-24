package org.example.natlex_task.domain.model;

import jakarta.persistence.*;
import lombok.*;
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
@Table(name = "import_jobs")
@EqualsAndHashCode
public class ImportJob {
    @Id
    @Column(name = "job_id", updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
    @JdbcTypeCode(value = VARCHAR)
    private UUID jobId;

    private JobStatus jobStatus;

    private String errorMessage;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections;
}
