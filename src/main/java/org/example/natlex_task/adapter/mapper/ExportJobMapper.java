package org.example.natlex_task.adapter.mapper;

import org.example.natlex_task.adapter.dto.ExportJobDto;
import org.example.natlex_task.domain.model.ExportJob;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ExportJobMapper {
    ExportJobMapper exportJobMapper = Mappers.getMapper(ExportJobMapper.class);

    ExportJobDto toDto(ExportJob exportJob);
}
