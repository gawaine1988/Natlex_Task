package org.example.natlex_task.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jdk.jfr.Enabled;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;

import java.util.UUID;

import static java.sql.Types.VARCHAR;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "geological_class")
public class GeologicalClass {
    @Id
    @Column(name = "geological_class_id", updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
    @JdbcTypeCode(value = VARCHAR)
    private UUID geologicalClassId;

    private String name;
    private String code;
}
