package org.example.natlex_task.adapter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class SectionDto {
    private UUID sectionId;

    @NotBlank(message = "Section name cannot be blank or null")
    private String name;

    @NotEmpty(message = "GeologicalClasses cannot be empty")
    private List<GeologicalClassDto> geologicalClasses;
}
