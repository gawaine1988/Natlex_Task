package org.example.natlex_task.adapter;

import org.example.natlex_task.adapter.dto.ApiResponse;
import org.example.natlex_task.domain.Section;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/sections", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class SectionController {
    @PostMapping
    public ApiResponse<UUID> createSection(@RequestBody Section section){
        return ApiResponse.ok();
    }
}
