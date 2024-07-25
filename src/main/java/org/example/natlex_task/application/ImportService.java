package org.example.natlex_task.application;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.natlex_task.application.exception.ResourceNotFoundException;
import org.example.natlex_task.domain.model.GeologicalClass;
import org.example.natlex_task.domain.model.ImportJob;
import org.example.natlex_task.domain.model.JobStatus;
import org.example.natlex_task.domain.model.Section;
import org.example.natlex_task.domain.repository.ImportJobRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ImportService {

    private final ImportJobRepository importJobRepository;
    private static final Pattern HEADER_PATTERN = Pattern.compile(
            "^Section name(, Class \\d+ name, Class \\d+ code)*$"
    );

    public UUID importFile(MultipartFile file) {
        ImportJob importJob = createImportFileJob();
        createImportJobSections(file, importJob);
        return importJob.getJobId();
    }

    private ImportJob createImportFileJob() {
        UUID jobId = UUID.randomUUID();
        ImportJob job = ImportJob.builder().jobId(jobId).jobStatus(JobStatus.IN_PROGRESS).build();
        importJobRepository.save(job);
        return job;
    }

    @Async
    protected void createImportJobSections(MultipartFile file, ImportJob importJob) {
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            if (isValidaFileHeader(sheet, importJob) && isValidaFileContent(sheet, importJob)) {
                importJob.setJobStatus(JobStatus.DONE);
                List<Section> sections = extractFileContent(sheet, importJob);
                importJob.setSections(sections);
                importJobRepository.save(importJob);
            }
        } catch (IOException e) {
            setErrorStatus(importJob, "Open xlsx file error.");
        }
    }

    private boolean isValidaFileContent(Sheet sheet, ImportJob importJob) {
        int rowCount = sheet.getPhysicalNumberOfRows();

        // Iterate through rows, starting from the second row (index 1)
        for (int i = 1; i < rowCount; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                setErrorStatus(importJob, "The file contains empty rows.");
                return false;
            }

            Cell sectionNameCell = row.getCell(0);
            if (sectionNameCell == null || sectionNameCell.getCellType() == CellType.BLANK) {
                setErrorStatus(importJob, "The file contains empty section name.");
                return false;
            }
        }
        return true;
    }

    private List<Section> extractFileContent(Sheet sheet, ImportJob importJob) {
        List<Section> sections = new ArrayList<>();
        Row headerRow = sheet.getRow(0);
        int numColumns = headerRow.getLastCellNum();
        String[] headers = new String[numColumns];

        for (int i = 0; i < numColumns; i++) {
            Cell cell = headerRow.getCell(i);
            if (cell != null) {
                headers[i] = cell.getStringCellValue().trim();
            }
        }

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                Section section = mapRowToSection(row, headers);
                section.setImportJob(importJob);
                sections.add(section);
            }
        }
        return sections;
    }

    private Section mapRowToSection(Row row, String[] headers) {
        Section.SectionBuilder sectionBuilder = Section.builder();
        List<GeologicalClass> geologicalClasses = new ArrayList<>();

        for (int i = 0; i < headers.length; i++) {
            Cell cell = row.getCell(i);
            String cellValue = cell != null ? cell.getStringCellValue().trim() : Strings.EMPTY;

            if (headers[i].equals("Section name")) {
                sectionBuilder.name(cellValue);
                sectionBuilder.sectionId(UUID.randomUUID());
            } else if (headers[i].startsWith("Class") && headers[i].contains("name")) {
                GeologicalClass.GeologicalClassBuilder geologicalClassBuilder = GeologicalClass.builder();
                geologicalClassBuilder.geologicalClassId(UUID.randomUUID());
                geologicalClassBuilder.name(cellValue);

                if (i + 1 < headers.length && headers[i + 1].startsWith("Class") && headers[i + 1].contains("code")) {
                    Cell codeCell = row.getCell(i + 1);
                    String codeValue = codeCell != null ? codeCell.getStringCellValue().trim() : Strings.EMPTY;
                    if (cellValue.isBlank() && codeValue.isBlank()) {
                        continue;
                    }
                    geologicalClassBuilder.code(codeValue);
                }

                GeologicalClass geologicalClass = geologicalClassBuilder.build();
                geologicalClasses.add(geologicalClass);
                i++;
            }
        }

        Section section = sectionBuilder.geologicalClasses(geologicalClasses).build();
        geologicalClasses.forEach(gc -> gc.setSection(section));

        return section;
    }

    private void setErrorStatus(ImportJob importJob, String errorMessage) {
        importJob.setErrorMessage(errorMessage);
        importJob.setJobStatus(JobStatus.ERROR);
        importJobRepository.save(importJob);
    }

    private boolean isValidaFileHeader(Sheet sheet, ImportJob importJob) {
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            setErrorStatus(importJob, "The file do not have header.");
            return false;
        }

        StringBuilder headerString = new StringBuilder();
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            if (headerRow.getCell(i) != null) {
                headerString.append(headerRow.getCell(i).getStringCellValue().trim());
                if (i < headerRow.getLastCellNum() - 1) {
                    headerString.append(", ");
                }
            }else {
                setErrorStatus(importJob, "The file header is missing.");
                return false;
            }
        }

        boolean matches = HEADER_PATTERN.matcher(headerString.toString()).matches();
        if (!matches) {
            setErrorStatus(importJob, "The file header is not correct.");
            return false;
        }
        return true;
    }

    public ImportJob findJobById(UUID uuid) {
        Optional<ImportJob> importJob = importJobRepository.findById(uuid);
        if(importJob.isEmpty()){
            throw new ResourceNotFoundException(String.format("Can not find the job by id: %s", uuid));
        }
        return importJob.get();
    }
}
