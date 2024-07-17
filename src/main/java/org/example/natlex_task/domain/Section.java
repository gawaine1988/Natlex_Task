package org.example.natlex_task.domain;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Section {

    private String name;
    private List<GeologicalClass> geologicalClasses;
}
