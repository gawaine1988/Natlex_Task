package org.example.natlex_task.adapter.mapper;

import org.example.natlex_task.adapter.dto.ImportJobDto;
import org.example.natlex_task.domain.model.ImportJob;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ImportJobMapper {
    ImportJobMapper importJobMapper = Mappers.getMapper(ImportJobMapper.class);

    ImportJobDto toDto(ImportJob importJob);
}
