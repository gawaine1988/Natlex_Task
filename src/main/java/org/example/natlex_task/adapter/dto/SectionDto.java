package org.example.natlex_task.adapter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SectionDto {

    @NotBlank(message = "Section name cannot be blank or null")
    private String name;

    @NotEmpty(message = "GeologicalClasses cannot be empty")
    private List<GeologicalClassDto> geologicalClasses;
}
