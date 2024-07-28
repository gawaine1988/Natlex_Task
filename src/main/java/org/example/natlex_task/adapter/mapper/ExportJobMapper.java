package org.example.natlex_task.adapter.mapper;

import org.example.natlex_task.adapter.dto.ExportJobDto;
import org.example.natlex_task.adapter.dto.GeologicalClassDto;
import org.example.natlex_task.adapter.dto.SectionDto;
import org.example.natlex_task.domain.model.ExportJob;
import org.example.natlex_task.domain.model.GeologicalClass;
import org.example.natlex_task.domain.model.Section;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ExportJobMapper {
    ExportJobMapper exportJobMapper = Mappers.getMapper(ExportJobMapper.class);

    ExportJobDto toDto(ExportJob exportJob);
}
