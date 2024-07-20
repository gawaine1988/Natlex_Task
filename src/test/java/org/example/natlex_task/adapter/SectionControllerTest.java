package org.example.natlex_task.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.example.natlex_task.adapter.dto.GeologicalClassDto;
import org.example.natlex_task.adapter.dto.SectionDto;
import org.example.natlex_task.domain.model.GeologicalClass;
import org.example.natlex_task.domain.model.Section;
import org.example.natlex_task.domain.repository.SectionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.ArrayList;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(properties = "server.port=8081")
class SectionControllerTest {

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper mapper;

    @Autowired
    SectionRepository sectionRepository;

    private static final String SECTION_PATH = "/sections";

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @SneakyThrows
    void should_create_section() {
        //Given
        GeologicalClassDto gc1 = GeologicalClassDto.builder().code("GC11").name("Geo Class 11").build();
        GeologicalClassDto gc2 = GeologicalClassDto.builder().code("GC12").name("Geo Class 12").build();
        ArrayList<GeologicalClassDto> geologicalClasses = new ArrayList<>() {{
            add(gc1);
            add(gc2);
        }};
        SectionDto section = SectionDto.builder().name("Section 1").geologicalClasses(geologicalClasses).build();
        String sectionJson = mapper.writeValueAsString(section);

        //When
        String requestUrl = "http://localhost:8081" + SECTION_PATH;
        MockHttpServletRequestBuilder content = post(requestUrl).contentType(APPLICATION_JSON).content(sectionJson);
        ResultActions response = mvc.perform(content);

        //Then
        response.andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.response").value(hasLength(36)));
    }

    @Test
    @SneakyThrows
    void should_response_error_when_section_name_is_blank() {
        //Given
        GeologicalClassDto gc1 = GeologicalClassDto.builder().code("GC11").name("Geo Class 11").build();
        GeologicalClassDto gc2 = GeologicalClassDto.builder().code("GC12").name("Geo Class 12").build();
        ArrayList<GeologicalClassDto> geologicalClasses = new ArrayList<>() {{
            add(gc1);
            add(gc2);
        }};
        SectionDto section = SectionDto.builder().name("").geologicalClasses(geologicalClasses).build();
        String sectionJson = mapper.writeValueAsString(section);

        //When
        String requestUrl = "http://localhost:8081" + SECTION_PATH;
        MockHttpServletRequestBuilder content = post(requestUrl).contentType(APPLICATION_JSON).content(sectionJson);
        ResultActions response = mvc.perform(content);

        //Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode", is(BAD_REQUEST.value())))
                .andExpect(jsonPath("$.statusMessage", is("[Section name cannot be blank or null]")));


    }


    @Test
    @SneakyThrows
    void should_find_section() {
        //Given
        UUID geologicalUuid = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();
        GeologicalClass gc = GeologicalClass.builder().geologicalClassId(geologicalUuid).code("GC11").name("Geo Class 11").build();
        ArrayList<GeologicalClass> geologicalClasses = new ArrayList<>() {{
            add(gc);
        }};
        Section section = Section.builder().sectionId(sectionId).name("Section 1").geologicalClasses(geologicalClasses).build();
        sectionRepository.save(section);

        //When
        String requestUrl = "http://localhost:8081" + SECTION_PATH + "/" + sectionId;
        MockHttpServletRequestBuilder content = get(requestUrl);
        ResultActions response = mvc.perform(content);

        //Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.response.sectionId").value(section.getSectionId().toString()))
                .andExpect(jsonPath("$.response.name").value(section.getName()))
                .andExpect(jsonPath("$.response.geologicalClasses",hasSize(1)))
                .andExpect(jsonPath("$.response.geologicalClasses[0].geologicalClassId").value(section.getGeologicalClasses().get(0).getGeologicalClassId().toString()))
                .andExpect(jsonPath("$.response.geologicalClasses[0].name").value(section.getGeologicalClasses().get(0).getName()))
                .andExpect(jsonPath("$.response.geologicalClasses[0].code").value(section.getGeologicalClasses().get(0).getCode()));
    }


    @Test
    @SneakyThrows
    void should_report_not_found_when_section_id_not_exist() {
        //Given
        UUID geologicalUuid = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();
        GeologicalClass gc = GeologicalClass.builder().geologicalClassId(geologicalUuid).code("GC11").name("Geo Class 11").build();
        ArrayList<GeologicalClass> geologicalClasses = new ArrayList<>() {{
            add(gc);
        }};
        Section section = Section.builder().sectionId(sectionId).name("Section 1").geologicalClasses(geologicalClasses).build();
        sectionRepository.save(section);

        //When
        String requestUrl = "http://localhost:8081" + SECTION_PATH + "/" + UUID.randomUUID();
        MockHttpServletRequestBuilder content = get(requestUrl);
        ResultActions response = mvc.perform(content);

        //Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.statusMessage").value("Can not find the section."));

    }
}