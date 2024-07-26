package org.example.natlex_task.application;

import lombok.SneakyThrows;
import org.example.natlex_task.domain.model.*;
import org.example.natlex_task.domain.repository.ExportJobRepository;
import org.example.natlex_task.domain.repository.SectionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.InjectMocks;

import org.example.natlex_task.domain.repository.ImportJobRepository;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.Unsafe;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.example.natlex_task.application.FileService.EXPORT_FILE_PAH;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FileServiceTest {
    @Mock
    private ImportJobRepository importJobRepository;

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private ExportJobRepository exportJobRepository;

    @InjectMocks
    private FileService fileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @SneakyThrows
    public void shouldImportFile() {
        //Given
        MultipartFile multipartFile = generateMutipartFile("src/test/resources/testfile.xlsx");

        // When
        when(importJobRepository.save(any(ImportJob.class))).thenAnswer(invocation -> invocation.getArgument(0));
        UUID jobId = fileService.importFile(multipartFile);

        // Then
        ArgumentCaptor<ImportJob> importJobCaptor = ArgumentCaptor.forClass(ImportJob.class);
        verify(importJobRepository, times(2)).save(importJobCaptor.capture());

        ImportJob savedImportJob = importJobCaptor.getValue();
        assertEquals(jobId, savedImportJob.getJobId());
        assertEquals(JobStatus.DONE, savedImportJob.getJobStatus());
        assertEquals(3, savedImportJob.getSections().size());
    }

    @Test
    @SneakyThrows
    public void should_report_error_and_not_import_when_import_all_header_missing_file() {
        //Given
        MultipartFile multipartFile = generateMutipartFile("src/test/resources/test_missing_all_header_file.xlsx");

        // When
        when(importJobRepository.save(any(ImportJob.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UUID jobId = fileService.importFile(multipartFile);

        // Then
        ArgumentCaptor<ImportJob> importJobCaptor = ArgumentCaptor.forClass(ImportJob.class);
        verify(importJobRepository, times(2)).save(importJobCaptor.capture());

        ImportJob savedImportJob = importJobCaptor.getValue();
        assertEquals(jobId, savedImportJob.getJobId());
        assertEquals(JobStatus.ERROR, savedImportJob.getJobStatus());
        assertEquals("The file do not have header.", savedImportJob.getErrorMessage());
        assertNull(savedImportJob.getSections());

    }

    @Test
    @SneakyThrows
    public void should_report_error_and_not_import_when_import_class_code_header_missing_file() {
        //Given
        MultipartFile multipartFile = generateMutipartFile("src/test/resources/test_missing_class_code_header_file.xlsx");

        // When
        when(importJobRepository.save(any(ImportJob.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UUID jobId = fileService.importFile(multipartFile);

        // Then
        ArgumentCaptor<ImportJob> importJobCaptor = ArgumentCaptor.forClass(ImportJob.class);
        verify(importJobRepository, times(2)).save(importJobCaptor.capture());

        ImportJob savedImportJob = importJobCaptor.getValue();
        assertEquals(jobId, savedImportJob.getJobId());
        assertEquals(JobStatus.ERROR, savedImportJob.getJobStatus());
        assertEquals("The file header is missing.", savedImportJob.getErrorMessage());
        assertNull(savedImportJob.getSections());
    }

    @Test
    @SneakyThrows
    public void should_report_error_and_not_import_when_import_class_name_header_missing_file() {
        //Given
        MultipartFile multipartFile = generateMutipartFile("src/test/resources/test_missing_class_name_header_file.xlsx");

        // When
        when(importJobRepository.save(any(ImportJob.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UUID jobId = fileService.importFile(multipartFile);

        // Then
        ArgumentCaptor<ImportJob> importJobCaptor = ArgumentCaptor.forClass(ImportJob.class);
        verify(importJobRepository, times(2)).save(importJobCaptor.capture());

        ImportJob savedImportJob = importJobCaptor.getValue();
        assertEquals(jobId, savedImportJob.getJobId());
        assertEquals(JobStatus.ERROR, savedImportJob.getJobStatus());
        assertEquals("The file header is missing.", savedImportJob.getErrorMessage());
        assertNull(savedImportJob.getSections());

    }

    @Test
    @SneakyThrows
    public void should_report_error_and_not_import_when_import_class_name_and_code_header_missing_file() {
        //Given
        MultipartFile multipartFile = generateMutipartFile("src/test/resources/test_missing_class_name_and_code_header_file.xlsx");

        // When
        when(importJobRepository.save(any(ImportJob.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UUID jobId = fileService.importFile(multipartFile);

        // Then
        ArgumentCaptor<ImportJob> importJobCaptor = ArgumentCaptor.forClass(ImportJob.class);
        verify(importJobRepository, times(2)).save(importJobCaptor.capture());

        ImportJob savedImportJob = importJobCaptor.getValue();
        assertEquals(jobId, savedImportJob.getJobId());
        assertEquals(JobStatus.ERROR, savedImportJob.getJobStatus());
        assertEquals("The file header is missing.", savedImportJob.getErrorMessage());
        assertNull(savedImportJob.getSections());

    }

    @Test
    @SneakyThrows
    public void should_report_error_and_not_import_when_import_section_name_header_missing_file() {
        //Given
        MultipartFile multipartFile = generateMutipartFile("src/test/resources/test_missing_section_name_header_file.xlsx");

        // When
        when(importJobRepository.save(any(ImportJob.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UUID jobId = fileService.importFile(multipartFile);

        // Then
        ArgumentCaptor<ImportJob> importJobCaptor = ArgumentCaptor.forClass(ImportJob.class);
        verify(importJobRepository, times(2)).save(importJobCaptor.capture());

        ImportJob savedImportJob = importJobCaptor.getValue();
        assertEquals(jobId, savedImportJob.getJobId());
        assertEquals(JobStatus.ERROR, savedImportJob.getJobStatus());
        assertEquals("The file header is missing.", savedImportJob.getErrorMessage());
        assertNull(savedImportJob.getSections());

    }

    @Test
    @SneakyThrows
    public void should_report_error_and_not_import_when_import_section_name_incorrect_file() {
        //Given
        MultipartFile multipartFile = generateMutipartFile("src/test/resources/test_incorrect_section_name_header_file.xlsx");

        // When
        when(importJobRepository.save(any(ImportJob.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UUID jobId = fileService.importFile(multipartFile);

        // Then
        ArgumentCaptor<ImportJob> importJobCaptor = ArgumentCaptor.forClass(ImportJob.class);
        verify(importJobRepository, times(2)).save(importJobCaptor.capture());

        ImportJob savedImportJob = importJobCaptor.getValue();
        assertEquals(jobId, savedImportJob.getJobId());
        assertEquals(JobStatus.ERROR, savedImportJob.getJobStatus());
        assertEquals("The file header is not correct.", savedImportJob.getErrorMessage());
        assertNull(savedImportJob.getSections());

    }

    @Test
    @SneakyThrows
    public void should_report_error_and_not_import_when_import_class_name_incorrect_file() {
        //Given
        MultipartFile multipartFile = generateMutipartFile("src/test/resources/test_incorrect_class_name_header_file.xlsx");

        // When
        when(importJobRepository.save(any(ImportJob.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UUID jobId = fileService.importFile(multipartFile);

        // Then
        ArgumentCaptor<ImportJob> importJobCaptor = ArgumentCaptor.forClass(ImportJob.class);
        verify(importJobRepository, times(2)).save(importJobCaptor.capture());

        ImportJob savedImportJob = importJobCaptor.getValue();
        assertEquals(jobId, savedImportJob.getJobId());
        assertEquals(JobStatus.ERROR, savedImportJob.getJobStatus());
        assertEquals("The file header is not correct.", savedImportJob.getErrorMessage());
        assertNull(savedImportJob.getSections());

    }

    @Test
    @SneakyThrows
    public void should_report_error_and_not_import_when_import_class_code_incorrect_file() {
        //Given
        MultipartFile multipartFile = generateMutipartFile("src/test/resources/test_incorrect_class_code_header_file.xlsx");

        // When
        when(importJobRepository.save(any(ImportJob.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UUID jobId = fileService.importFile(multipartFile);

        // Then
        ArgumentCaptor<ImportJob> importJobCaptor = ArgumentCaptor.forClass(ImportJob.class);
        verify(importJobRepository, times(2)).save(importJobCaptor.capture());

        ImportJob savedImportJob = importJobCaptor.getValue();
        assertEquals(jobId, savedImportJob.getJobId());
        assertEquals(JobStatus.ERROR, savedImportJob.getJobStatus());
        assertEquals("The file header is not correct.", savedImportJob.getErrorMessage());
        assertNull(savedImportJob.getSections());

    }

    private static MultipartFile generateMutipartFile(String filePath) throws IOException {
        File file = new File(filePath);

        FileInputStream fileStream = new FileInputStream(file);

        MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", fileStream);
        return multipartFile;
    }

    @Test
    @SneakyThrows
    public void shouldExportFile() {
        // Given
        GeologicalClass gc1 = GeologicalClass.builder().geologicalClassId(UUID.randomUUID()).code("GC11").name("Geo Class 11").build();
        GeologicalClass gc2 = GeologicalClass.builder().geologicalClassId(UUID.randomUUID()).code("GC22").name("Geo Class 22").build();
        GeologicalClass gc3 = GeologicalClass.builder().geologicalClassId(UUID.randomUUID()).code("GC33").name("Geo Class 33").build();
        ArrayList<GeologicalClass> geologicalClasses1 = new ArrayList<>() {{
            add(gc1);
            add(gc2);
        }};

        ArrayList<GeologicalClass> geologicalClasses2 = new ArrayList<>() {{
            add(gc3);
        }};

        Section section1 = Section.builder().sectionId(UUID.randomUUID()).name("Section 1").geologicalClasses(geologicalClasses1).build();
        Section section2 = Section.builder().sectionId(UUID.randomUUID()).name("Section 2").geologicalClasses(geologicalClasses2).build();

        List<Section> savedSections = new ArrayList<>(){{
            add(section1);
            add(section2);
        }};

        // When
        when(sectionRepository.findAll()).thenReturn(savedSections);

        when(exportJobRepository.save(any(ExportJob.class))).thenAnswer(invocation -> invocation.getArgument(0));
        UUID jobId = fileService.startExportFileJob();

        // Then
        ArgumentCaptor<ExportJob> exportJobCaptor = ArgumentCaptor.forClass(ExportJob.class);
        verify(exportJobRepository, times(2)).save(exportJobCaptor.capture());

        ExportJob exportJob = exportJobCaptor.getValue();
        assertEquals(jobId, exportJob.getJobId());
        assertEquals(JobStatus.DONE, exportJob.getJobStatus());
        assertTrue(Files.exists(Paths.get(EXPORT_FILE_PAH+jobId+".xlsx")));

    }
}