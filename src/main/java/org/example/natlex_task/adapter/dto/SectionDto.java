package org.example.natlex_task.adapter.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SectionDto {
    private String name;
    private List<GeologicalClassDto> geologicalClasses;
}
