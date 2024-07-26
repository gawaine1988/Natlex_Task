package org.example.natlex_task.application;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.natlex_task.application.exception.ResourceNotFoundException;
import org.example.natlex_task.domain.model.*;
import org.example.natlex_task.domain.repository.ExportJobRepository;
import org.example.natlex_task.domain.repository.ImportJobRepository;
import org.example.natlex_task.domain.repository.SectionRepository;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class FileService {

    public static final String EXPORT_FILE_PAH = "src/ExportedFiles/";
    private final ImportJobRepository importJobRepository;
    private final ExportJobRepository exportJobRepository;
    private final SectionRepository sectionRepository;
    private static final Pattern HEADER_PATTERN = Pattern.compile(
            "^Section name(, Class \\d+ code, Class \\d+ name)*$"
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
            } else if (headers[i].startsWith("Class") && headers[i].contains("code")) {
                GeologicalClass.GeologicalClassBuilder geologicalClassBuilder = GeologicalClass.builder();
                geologicalClassBuilder.geologicalClassId(UUID.randomUUID());
                geologicalClassBuilder.code(cellValue);

                if (i + 1 < headers.length && headers[i + 1].startsWith("Class") && headers[i + 1].contains("name")) {
                    Cell codeCell = row.getCell(i + 1);
                    String codeValue = codeCell != null ? codeCell.getStringCellValue().trim() : Strings.EMPTY;
                    if (cellValue.isBlank() && codeValue.isBlank()) {
                        continue;
                    }
                    geologicalClassBuilder.name(codeValue);
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
            } else {
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

    public ImportJob getImportJobById(UUID uuid) {
        Optional<ImportJob> importJob = importJobRepository.findById(uuid);
        if (importJob.isEmpty()) {
            throw new ResourceNotFoundException(String.format("Can not find the import job by id: %s", uuid));
        }
        return importJob.get();
    }

    public UUID startExportFileJob() {
        ExportJob exportJob = createExportFileJob();
        exportFile(exportJob);
        return exportJob.getJobId();
    }

    @Async
    protected void exportFile(ExportJob exportJob) {
        try {
            List<Section> sections = sectionRepository.findAll();
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Sections");

            createHeaderRow(sheet, sections);

            int rowNum = 1;
            for (Section section : sections) {
                Row row = sheet.createRow(rowNum++);
                writeSectionData(section, row);
            }

            File file = new File(EXPORT_FILE_PAH + exportJob.getJobId() + ".xlsx");
            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                workbook.write(fileOut);
            }

            workbook.close();

            exportJob.setJobStatus(JobStatus.DONE);
            exportJob.setFilePath(file.getAbsolutePath());
        } catch (IOException e) {
            exportJob.setJobStatus(JobStatus.ERROR);
            exportJob.setErrorMessage("Error writing XLSX file: " + e.getMessage());
        }

        exportJobRepository.save(exportJob);
    }

    private void createHeaderRow(Sheet sheet, List<Section> sections) {

        Optional<Integer> max = sections.stream()
                .map(section -> section.getGeologicalClasses().size())
                .max(Comparator.naturalOrder());

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Section name");

        if (max.isPresent()) {
            int geologicalStartColumn = 1;
            for (int i = 0; i < max.get(); i++) {
                headerRow.createCell(geologicalStartColumn++).setCellValue(String.format("Class %d code", i + 1));
                headerRow.createCell(geologicalStartColumn++).setCellValue(String.format("Class %d name", i + 2));
            }
        }
    }

    private void writeSectionData(Section section, Row row) {
        row.createCell(0).setCellValue(section.getName());
        // Add code to write geological classes
        List<GeologicalClass> geologicalClasses = section.getGeologicalClasses();

        int geologicalRowNumber = 1;
        for (int i = 0; i < geologicalClasses.size(); i++) {
            GeologicalClass geologicalClass = geologicalClasses.get(i);
            row.createCell(geologicalRowNumber++).setCellValue(geologicalClass.getCode());
            row.createCell(geologicalRowNumber++).setCellValue(geologicalClass.getName());
        }
    }

    private ExportJob createExportFileJob() {
        ExportJob exportJob = ExportJob.builder()
                .jobStatus(JobStatus.IN_PROGRESS)
                .jobId(UUID.randomUUID())
                .build();
        return exportJobRepository.save(exportJob);
    }

    public ExportJob getExportJobById(String id) {
        Optional<ExportJob> exportJob = exportJobRepository.findById(UUID.fromString(id));
        if (exportJob.isEmpty()) {
            throw new ResourceNotFoundException(String.format("Can not find the export job by id: %s", id));
        }
        return exportJob.get();
    }

    public Resource getExportFileById(String id) {
        return null;
    }
}
