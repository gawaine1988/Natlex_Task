package org.example.natlex_task.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class ImportService {

    public UUID importFile(MultipartFile file) {
        return UUID.randomUUID();
    }
}
