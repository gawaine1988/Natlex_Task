package org.example.natlex_task.adapter;

import jakarta.validation.Valid;
import org.example.natlex_task.adapter.dto.ApiResponse;
import org.example.natlex_task.adapter.dto.SectionDto;
import org.example.natlex_task.application.ImportService;
import org.example.natlex_task.domain.model.Section;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/", produces = APPLICATION_JSON_VALUE)
public class FileController {

    @Autowired
    ImportService importService;

    @PostMapping("/import")
    public ApiResponse<UUID> importFile(@RequestParam("file") MultipartFile file) {
        UUID jobId = importService.importFile(file);
        return ApiResponse.ok(jobId);
    }

}
