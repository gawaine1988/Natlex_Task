package org.example.natlex_task.adapter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.example.natlex_task.domain.model.JobStatus;

import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ImportJobDto {
    private UUID jobId;

    private JobStatus jobStatus;

    private String errorMessage;
}
