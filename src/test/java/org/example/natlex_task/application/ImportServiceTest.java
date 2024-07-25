package org.example.natlex_task.application;

import lombok.SneakyThrows;
import org.example.natlex_task.domain.model.ImportJob;
import org.example.natlex_task.domain.model.JobStatus;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ImportServiceTest {
    @Mock
    private ImportJobRepository importJobRepository;

    @InjectMocks
    private FileService importService;

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
        UUID jobId = importService.importFile(multipartFile);

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

        UUID jobId = importService.importFile(multipartFile);

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

        UUID jobId = importService.importFile(multipartFile);

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

        UUID jobId = importService.importFile(multipartFile);

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

        UUID jobId = importService.importFile(multipartFile);

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

        UUID jobId = importService.importFile(multipartFile);

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

        UUID jobId = importService.importFile(multipartFile);

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

        UUID jobId = importService.importFile(multipartFile);

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

        UUID jobId = importService.importFile(multipartFile);

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
}