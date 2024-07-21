package org.example.natlex_task.application;

import lombok.RequiredArgsConstructor;
import org.example.natlex_task.application.exception.ResourceNotFoundException;
import org.example.natlex_task.domain.model.Section;
import org.example.natlex_task.domain.repository.GeologicalClassRepository;
import org.example.natlex_task.domain.repository.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SectionService {
    private final SectionRepository sectionRepository;
    private final GeologicalClassRepository geologicalClassRepository;

    public UUID createSection(Section section) {
        Section savedSection = sectionRepository.save(section);
        return savedSection.getSectionId();
    }

    public Optional<Section> findSectionBuId(UUID sectionId) {
        return sectionRepository.findById(sectionId);
    }

    public UUID updateSection(Section section) {
        validateId(section);
        Section updatedSection = sectionRepository.save(section);
        return updatedSection.getSectionId();
    }

    private void validateId(Section section) {
        sectionRepository
                .findById(section.getSectionId())
                .orElseThrow(()-> new ResourceNotFoundException(String.format("Section id: %s do not exist.", section.getSectionId())));

        section.getGeologicalClasses()
                .forEach(p-> {
                    if(geologicalClassRepository.findById(p.getGeologicalClassId()).isEmpty()){
                        throw new ResourceNotFoundException(String.format("Geological id: %s do not exist.", p.getGeologicalClassId()));
                    }
                });
    }
}
