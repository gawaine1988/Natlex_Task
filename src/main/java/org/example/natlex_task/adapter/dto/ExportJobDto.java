package org.example.natlex_task.adapter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.example.natlex_task.domain.model.JobStatus;

import java.util.List;
import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ExportJobDto {
    private UUID jobId;

    private JobStatus jobStatus;

    private String errorMessage;
}
