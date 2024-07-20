package org.example.natlex_task.adapter.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GeologicalClassDto {

    private UUID geologicalClassId;

    @NotBlank(message = "Geological name cannot be blank or null")
    private String name;

    @NotBlank(message = "Geological code cannot be blank or null")
    private String code;
}
