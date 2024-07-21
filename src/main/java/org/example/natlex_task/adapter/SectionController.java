package org.example.natlex_task.adapter;

import jakarta.validation.Valid;
import org.example.natlex_task.adapter.dto.ApiResponse;
import org.example.natlex_task.adapter.dto.SectionDto;
import org.example.natlex_task.adapter.exception.ArgumentNotValidException;
import org.example.natlex_task.adapter.mapper.SectionMapper;
import org.example.natlex_task.application.SectionService;
import org.example.natlex_task.domain.model.Section;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/sections", produces = APPLICATION_JSON_VALUE)
public class SectionController {
    @Autowired
    SectionMapper sectionMapper;

    @Autowired
    SectionService sectionService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ApiResponse<UUID> createSection(@Valid @RequestBody SectionDto createSectionRequest) {
        Section section = sectionMapper.toModel(createSectionRequest);
        UUID id = sectionService.createSection(section);
        return ApiResponse.ok(id);
    }

    @GetMapping("/{sectionId}")
    public ApiResponse<SectionDto> getSectionById(@PathVariable("sectionId") String sectionId) {
        validateUUID(sectionId);

        Optional<Section> section = sectionService.findSectionById(UUID.fromString(sectionId));
        if (section.isPresent()) {
            SectionDto sectionDto = sectionMapper.toDto(section.get());
            return ApiResponse.ok(sectionDto);
        } else {
            return ApiResponse.buildResponse(HttpStatus.NOT_FOUND, "Can not find the section.", null);
        }
    }

    @PutMapping(consumes = APPLICATION_JSON_VALUE)
    public ApiResponse<UUID> updateSection(@Valid @RequestBody SectionDto updateSectionRequest) {
        validateUpdateRequest(updateSectionRequest);
        Section section = sectionMapper.toModel(updateSectionRequest);
        UUID id = sectionService.updateSection(section);
        return ApiResponse.ok(id);
    }

    @DeleteMapping("/{sectionId}")
    ApiResponse<Void> deleteSectionById(@PathVariable("sectionId") String sectionId) {
        validateUUID(sectionId);
        sectionService.delete(UUID.fromString(sectionId));
        return ApiResponse.ok();
    }
    private void validateUpdateRequest(SectionDto updateSectionRequest) {
        if (updateSectionRequest.getSectionId() == null) {
            throw new ArgumentNotValidException("Section id cannot be null when update the section");
        }
        updateSectionRequest.getGeologicalClasses()
                .stream()
                .forEach(geologicalClassDto -> {
                    if(geologicalClassDto.getGeologicalClassId() == null){
                        throw new ArgumentNotValidException("Geological id cannot be null when update the section");
                    }
                });

    }

    private static void validateUUID(String sectionId) {
        try {
            UUID uuid = UUID.fromString(sectionId);
            // Validate the format is correct
        } catch (IllegalArgumentException e) {
            throw new ArgumentNotValidException("Invalid Section Id");
        }
    }
}
