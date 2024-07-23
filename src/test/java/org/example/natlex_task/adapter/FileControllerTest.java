package org.example.natlex_task.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.example.natlex_task.domain.model.Section;
import org.example.natlex_task.domain.repository.GeologicalClassRepository;
import org.example.natlex_task.domain.repository.SectionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
    @SneakyThrows
    void should_create_job_and_import_file() {
        //Given
        UUID jobId = UUID.randomUUID();
        byte[] content = Files.readAllBytes(Paths.get("src/test/resources/testfile.xlsx"));
        mockFile = new MockMultipartFile("file", "testfile.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", content);

        //When
        ResultActions response = mvc.perform(MockMvcRequestBuilders.multipart("/import")
                .file(mockFile));

        //Then
        response.andExpect(status().isOk())
                .andExpect(content().string(jobId.toString()));

        List<Section> all = sectionRepository.findAll();
        assertEquals(all.size(),4);
    }

    @Test
    @SneakyThrows
    void should_report_error_when_missing_header() {
        //Given
        byte[] missingALlHeaderContent = Files.readAllBytes(Paths.get("src/test/resources/test_missing_all_header_file.xlsx"));
        byte[] missingClassNameHeaderContent = Files.readAllBytes(Paths.get("src/test/resources/test_missing_class_name_header_file.xlsx"));
        byte[] missingClassCodeHeaderContent = Files.readAllBytes(Paths.get("src/test/resources/test_missing_class_code_header_file.xlsx"));
        byte[] missingClassCodeAndHeaderContent = Files.readAllBytes(Paths.get("src/test/resources/test_missing_class_code_and_header_file.xlsx"));
        byte[] missingSectionNameHeaderContent = Files.readAllBytes(Paths.get("src/test/resources/test_missing_section_name_header_file.xlsx"));


        //When
        mockFile = new MockMultipartFile("file", "test_missing_all_header_file.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", missingALlHeaderContent);
        ResultActions missingAllHeaderResponse = mvc.perform(MockMvcRequestBuilders.multipart("/import")
                .file(mockFile));

        mockFile = new MockMultipartFile("file", "test_missing_class_name_header_file.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", missingClassNameHeaderContent);
        ResultActions missingClassNameHeaderResponse = mvc.perform(MockMvcRequestBuilders.multipart("/import")
                .file(mockFile));

        mockFile = new MockMultipartFile("file", "test_missing_class_code_header_file.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", missingClassCodeHeaderContent);
        ResultActions missingClassCodeHeaderResponse = mvc.perform(MockMvcRequestBuilders.multipart("/import")
                .file(mockFile));

        mockFile = new MockMultipartFile("file", "test_missing_class_name_and_code_header_file.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", missingClassCodeAndHeaderContent);
        ResultActions missingClassCodeAndNameHeaderResponse = mvc.perform(MockMvcRequestBuilders.multipart("/import")
                .file(mockFile));


        mockFile = new MockMultipartFile("file", "test_missing_section_name_header_file.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", missingSectionNameHeaderContent);
        ResultActions missingSectionNameHeaderResponse = mvc.perform(MockMvcRequestBuilders.multipart("/import")
                .file(mockFile));




        //Then
        missingAllHeaderResponse.andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode", is(BAD_REQUEST.value())))
                .andExpect(jsonPath("$.statusMessage", is("The header should contain section name, class name and class code")));

        missingClassNameHeaderResponse.andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode", is(BAD_REQUEST.value())))
                .andExpect(jsonPath("$.statusMessage", is("The header should contain section name, class name and class code")));

        missingClassCodeHeaderResponse.andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode", is(BAD_REQUEST.value())))
                .andExpect(jsonPath("$.statusMessage", is("The header should contain section name, class name and class code")));

        missingClassCodeAndNameHeaderResponse.andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode", is(BAD_REQUEST.value())))
                .andExpect(jsonPath("$.statusMessage", is("The header should contain section name, class name and class code")));

        missingSectionNameHeaderResponse.andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode", is(BAD_REQUEST.value())))
                .andExpect(jsonPath("$.statusMessage", is("The header should contain section name, class name and class code")));

    }

    @Test
    @SneakyThrows
    void should_report_error_when_header_name_incorrect() {
        //Given
        byte[] incorrectSectionNameContent = Files.readAllBytes(Paths.get("src/test/resources/testfile_section_name_header_incorrect.xlsx"));
        byte[] incorrectClassNameContent = Files.readAllBytes(Paths.get("src/test/resources/testfile_class_name_header_incorrect.xlsx"));
        byte[] incorrectClassCodeContent = Files.readAllBytes(Paths.get("src/test/resources/testfile_class_code_header_incorrect.xlsx"));


        //When
        mockFile = new MockMultipartFile("file", "testfile_section_name_header_incorrect.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", incorrectSectionNameContent);
        ResultActions incorrectSectionNameResponse = mvc.perform(MockMvcRequestBuilders.multipart("/import")
                .file(mockFile));

        mockFile = new MockMultipartFile("file", "testfile_class_name_header_incorrect.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", incorrectClassNameContent);
        ResultActions incorrectClassNameResponse = mvc.perform(MockMvcRequestBuilders.multipart("/import")
                .file(mockFile));

        mockFile = new MockMultipartFile("file", "testfile_class_code_header_incorrect.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", incorrectClassCodeContent);
        ResultActions incorrectClassCodeResponse = mvc.perform(MockMvcRequestBuilders.multipart("/import")
                .file(mockFile));

        //Then
        incorrectSectionNameResponse.andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode", is(BAD_REQUEST.value())))
                .andExpect(jsonPath("$.statusMessage", is("The header is incorrect")));

        incorrectClassNameResponse.andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode", is(BAD_REQUEST.value())))
                .andExpect(jsonPath("$.statusMessage", is("The header is incorrect")));


        incorrectClassCodeResponse.andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode", is(BAD_REQUEST.value())))
                .andExpect(jsonPath("$.statusMessage", is("The header is incorrect")));

    }



}