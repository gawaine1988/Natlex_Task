package org.example.natlex_task.adapter;

import jakarta.validation.Valid;
import org.example.natlex_task.adapter.dto.ApiResponse;
import org.example.natlex_task.adapter.dto.SectionDto;
import org.example.natlex_task.adapter.mapper.SectionMapper;
import org.example.natlex_task.application.SectionService;
import org.example.natlex_task.domain.model.Section;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/sections", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class SectionController {
    @Autowired
    SectionMapper sectionMapper;

    @Autowired
    SectionService sectionService;

    @PostMapping
    public ApiResponse<UUID> createSection(@Valid @RequestBody SectionDto createSectionRequest) {
        Section section = sectionMapper.toModel(createSectionRequest);
        UUID id = sectionService.createSection(section);
        return ApiResponse.ok(id);
    }

    @GetMapping("/{sectionId}")
    public ApiResponse<SectionDto> getSectionById(@PathVariable("sectionId")  String sectionId) {
        return ApiResponse.ok();
    }
}
