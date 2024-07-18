package org.example.natlex_task.adapter.mapper;

import org.example.natlex_task.adapter.dto.GeologicalClassDto;
import org.example.natlex_task.adapter.dto.SectionDto;
import org.example.natlex_task.domain.model.GeologicalClass;
import org.example.natlex_task.domain.model.Section;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface SectionMapper {
    SectionMapper sectionMapper  = Mappers.getMapper(SectionMapper.class);
    @Mapping(target = "sectionId", expression = "java(generateUUID())")
    @Mapping(target = "geologicalClasses", source = "geologicalClasses", qualifiedByName = "createGeologicalClasses")
    Section toModel(SectionDto sectionDto);

    default UUID generateUUID() {
        return UUID.randomUUID();
    }

    @Named("createGeologicalClasses")
    default List<GeologicalClass> createGeologicalClasses(List<GeologicalClassDto> geologicalClassDtos) {
        return geologicalClassDtos.stream()
                .map(this::mapNested)
                .collect(Collectors.toList());
    }

    default GeologicalClass mapNested(GeologicalClassDto geologicalClassDto) {
        if (geologicalClassDto == null) {
            return null;
        }
        GeologicalClass geologicalClass = new GeologicalClass();
        geologicalClass.setCode(geologicalClassDto.getCode());
        geologicalClass.setName(geologicalClassDto.getName());
        geologicalClass.setGeologicalClassId(generateUUID());
        return geologicalClass;
    }
}
