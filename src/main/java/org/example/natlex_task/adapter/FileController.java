package org.example.natlex_task.adapter;

import org.example.natlex_task.adapter.dto.ApiResponse;
import org.example.natlex_task.application.FileService;
import org.example.natlex_task.domain.model.ExportJob;
import org.example.natlex_task.domain.model.ImportJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.example.natlex_task.adapter.utils.Utils.validateUUID;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping( produces = APPLICATION_JSON_VALUE)
public class FileController {
    @Autowired
    FileService fileService;

    @PostMapping("/import")
    public ApiResponse<UUID> importFile(@RequestParam("file") MultipartFile file) {
        UUID jobId = fileService.importFile(file);
        return ApiResponse.ok(jobId);
    }

    @GetMapping("/import/{id}")
    public ApiResponse<ImportJob> getSectionById(@PathVariable("id") String id) {
        validateUUID(id);
        ImportJob importJob = fileService.getImportJobById(UUID.fromString(id));
        return ApiResponse.ok(importJob);
    }

    @GetMapping("/export")
    public ApiResponse<UUID> getExportId() {
        UUID exportId = fileService.startExportFileJob();
        return ApiResponse.ok(exportId);
    }

    @GetMapping("/export/{id}")
    public ApiResponse<ExportJob> getExportJob(@PathVariable("id") String id) {
        validateUUID(id);
        ExportJob exportJob = fileService.getExportJobById(id);
        return ApiResponse.ok(exportJob);
    }

    @GetMapping("/export/{id}/file")
    public ResponseEntity<Resource> getExportFile(@PathVariable("id") String id) {
        validateUUID(id);
        Resource file  = fileService.getExportFileById(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

}
