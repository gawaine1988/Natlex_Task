package org.example.natlex_task.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.example.natlex_task.domain.model.ExportJob;
import org.example.natlex_task.domain.model.ImportJob;
import org.example.natlex_task.domain.model.JobStatus;
import org.example.natlex_task.domain.model.Section;
import org.example.natlex_task.domain.repository.ExportJobRepository;
import org.example.natlex_task.domain.repository.GeologicalClassRepository;
import org.example.natlex_task.domain.repository.ImportJobRepository;
import org.example.natlex_task.domain.repository.SectionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.hasLength;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(properties = "server.port=8081")
class FileControllerTest {
    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper mapper;

    private MockMultipartFile mockFile;

    @Autowired
    ImportJobRepository importJobRepository;

    @Autowired
    ExportJobRepository exportJobRepository;

    @Autowired
    SectionRepository sectionRepository;

    @Autowired
    GeologicalClassRepository geologicalClassRepository;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @Transactional
    @Rollback
    @SneakyThrows
    void should_create_job_and_import_file() {
        //Given
        byte[] content = Files.readAllBytes(Paths.get("src/test/resources/testfile.xlsx"));
        mockFile = new MockMultipartFile("file", "testfile.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", content);

        //When
        ResultActions response = mvc.perform(MockMvcRequestBuilders.multipart("/import")
                .file(mockFile));

        //Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value(hasLength(36)));
        TimeUnit.SECONDS.sleep(1);
        List<ImportJob> all = importJobRepository.findAll();
        assertEquals(1, all.size());
        assertEquals(JobStatus.DONE, all.get(0).getJobStatus());

        List<Section> sections = sectionRepository.findAll();
        assertEquals(3, sections.size());
        assertEquals("Section 1", sections.get(0).getName());
        assertEquals(2, sections.get(0).getGeologicalClasses().size());
        assertEquals("Geo Class 11", sections.get(0).getGeologicalClasses().get(0).getName());
        assertEquals("GC11", sections.get(0).getGeologicalClasses().get(0).getCode());
        assertEquals("Section 3", sections.get(2).getName());
        assertEquals(1, sections.get(2).getGeologicalClasses().size());
        assertEquals("Geo Class 32", sections.get(2).getGeologicalClasses().get(0).getName());
        assertEquals("GC32", sections.get(2).getGeologicalClasses().get(0).getCode());
    }

    @Test
    @Transactional
    @Rollback
    @SneakyThrows
    void should_report_error_when_missing_file_header() {
        //Given
        byte[] content = Files.readAllBytes(Paths.get("src/test/resources/test_missing_all_header_file.xlsx"));
        mockFile = new MockMultipartFile("file", "testfile.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", content);

        //When

        ResultActions response = mvc.perform(MockMvcRequestBuilders.multipart("/import")
                .file(mockFile));

        //Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value(hasLength(36)));
        TimeUnit.SECONDS.sleep(1);
        List<ImportJob> all = importJobRepository.findAll();
        assertEquals(1, all.size());
        assertEquals(JobStatus.ERROR, all.get(0).getJobStatus());
        assertEquals("The file do not have header.", all.get(0).getErrorMessage());
    }

    @Test
    @Transactional
    @Rollback
    @SneakyThrows
    void should_get_import_job_by_id() {
        // Given
        UUID importedJobId = UUID.randomUUID();
        ImportJob importedJob = ImportJob.builder()
                .jobId(importedJobId)
                .jobStatus(JobStatus.DONE)
                .build();
        importJobRepository.save(importedJob);

        // When
        String requestUrl = "/import/" + importedJobId;
        MockHttpServletRequestBuilder content = get(requestUrl);
        ResultActions response = mvc.perform(content);

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.response.jobId").value(importedJobId.toString()));
    }

    @Test
    @Transactional
    @Rollback
    @SneakyThrows
    void should_report_not_found_When_import_job_id_not_exist() {
        // Given
        UUID importedJobId = UUID.randomUUID();
        UUID notExistJobId = UUID.randomUUID();
        ImportJob importedJob = ImportJob.builder()
                .jobId(importedJobId)
                .jobStatus(JobStatus.DONE)
                .build();
        importJobRepository.save(importedJob);

        // When
        String requestUrl = "/import/" + notExistJobId;
        MockHttpServletRequestBuilder content = get(requestUrl);
        ResultActions response = mvc.perform(content);

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.statusMessage").value(String.format("Can not find the import job by id: %s", notExistJobId)));
    }

    @Test
    @Transactional
    @Rollback
    @SneakyThrows
    void should_create_export_job_and_return_job_id() {

        // When
        String requestUrl = "/export";
        MockHttpServletRequestBuilder content = get(requestUrl);
        ResultActions response = mvc.perform(content);

        // Then
        String exportResponse = response.andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.OK.value()))
                .andReturn()
                .getResponse()
                .getContentAsString();


        JsonNode jsonNode = mapper.readTree(exportResponse);
        String sectionId = jsonNode.path("response").asText();

        TimeUnit.SECONDS.sleep(1);
        List<ExportJob> all = exportJobRepository.findAll();
        assertEquals(1, all.size());
        assertEquals(JobStatus.DONE, all.get(0).getJobStatus());
        assertEquals(sectionId, all.get(0).getJobId().toString());
    }

    @Test
    @Transactional
    @Rollback
    @SneakyThrows
    void should_get_export_job_by_id() {
        // Given
        UUID exportedJobId = UUID.randomUUID();
        ExportJob exportJob = ExportJob.builder()
                .jobId(exportedJobId)
                .jobStatus(JobStatus.DONE)
                .build();
        exportJobRepository.save(exportJob);

        // When
        String requestUrl = "/export/" + exportedJobId;
        MockHttpServletRequestBuilder content = get(requestUrl);
        ResultActions response = mvc.perform(content);

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.response.jobId").value(exportedJobId.toString()));
    }

    @Test
    @Transactional
    @Rollback
    @SneakyThrows
    void should_report_not_found_When_export_job_id_not_exist() {
        // Given
        UUID exportedJobId = UUID.randomUUID();
        UUID notExistJobId = UUID.randomUUID();
        ExportJob exportedJob = ExportJob.builder()
                .jobId(exportedJobId)
                .jobStatus(JobStatus.DONE)
                .build();
        exportJobRepository.save(exportedJob);

        // When
        String requestUrl = "/export/" + notExistJobId;
        MockHttpServletRequestBuilder content = get(requestUrl);
        ResultActions response = mvc.perform(content);

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.statusMessage").value(String.format("Can not find the export job by id: %s", notExistJobId)));
    }
}