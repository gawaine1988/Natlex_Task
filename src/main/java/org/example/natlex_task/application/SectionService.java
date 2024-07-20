package org.example.natlex_task.application;

import lombok.RequiredArgsConstructor;
import org.example.natlex_task.domain.model.Section;
import org.example.natlex_task.domain.repository.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SectionService {
    private final SectionRepository sectionRepository;


    public UUID createSection(Section section) {
        Section savedSection = sectionRepository.save(section);
        return savedSection.getSectionId();
    }

    public Optional<Section> findSectionBuId(UUID sectionId) {
        return sectionRepository.findById(sectionId);
    }

    public UUID updateSection(Section section) {
        return null;
    }
}
