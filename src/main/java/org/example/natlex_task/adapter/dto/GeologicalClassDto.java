package org.example.natlex_task.adapter.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GeologicalClassDto {

    @NotBlank(message = "Geological name cannot be blank or null")
    private String name;

    @NotBlank(message = "Geological code cannot be blank or null")
    private String code;
}
