package org.example.natlex_task.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.example.natlex_task.adapter.dto.GeologicalClassDto;
import org.example.natlex_task.adapter.dto.SectionDto;
import org.example.natlex_task.domain.model.GeologicalClass;
import org.example.natlex_task.domain.model.Section;
import org.example.natlex_task.domain.repository.GeologicalClassRepository;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Autowired
    GeologicalClassRepository geologicalClassRepository;

    private static final String SECTION_URL = "http://localhost:8081/sections";

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
        SectionDto section = buildSectionDtoRequest();
        String sectionJson = mapper.writeValueAsString(section);

        //When
        MockHttpServletRequestBuilder content = post(SECTION_URL).contentType(APPLICATION_JSON).content(sectionJson);
        ResultActions response = mvc.perform(content);

        //Then
        response.andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.response").value(hasLength(36)));
    }

    private static SectionDto buildSectionDtoRequest() {
        GeologicalClassDto gc1 = GeologicalClassDto.builder().code("GC11").name("Geo Class 11").build();
        GeologicalClassDto gc2 = GeologicalClassDto.builder().code("GC12").name("Geo Class 12").build();
        ArrayList<GeologicalClassDto> geologicalClasses = new ArrayList<>() {{
            add(gc1);
            add(gc2);
        }};
        SectionDto section = SectionDto.builder().name("Section 1").geologicalClasses(geologicalClasses).build();
        return section;
    }

    @Test
    @SneakyThrows
    void should_response_error_when_post_with_blank_section_name() {
        //Given
        SectionDto sectionDtoRequest = buildSectionDtoRequest();
        sectionDtoRequest.setName("");
        String sectionJson = mapper.writeValueAsString(sectionDtoRequest);

        //When
        MockHttpServletRequestBuilder content = post(SECTION_URL).contentType(APPLICATION_JSON).content(sectionJson);
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
        Section section = saveSection(geologicalUuid, sectionId);

        //When
        MockHttpServletRequestBuilder content = get(SECTION_URL + "/" + sectionId);
        ResultActions response = mvc.perform(content);

        //Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.response.sectionId").value(section.getSectionId().toString()))
                .andExpect(jsonPath("$.response.name").value(section.getName()))
                .andExpect(jsonPath("$.response.geologicalClasses", hasSize(1)))
                .andExpect(jsonPath("$.response.geologicalClasses[0].geologicalClassId").value(section.getGeologicalClasses().get(0).getGeologicalClassId().toString()))
                .andExpect(jsonPath("$.response.geologicalClasses[0].name").value(section.getGeologicalClasses().get(0).getName()))
                .andExpect(jsonPath("$.response.geologicalClasses[0].code").value(section.getGeologicalClasses().get(0).getCode()));
    }

    private Section saveSection(UUID geologicalUuid, UUID sectionId) {
        GeologicalClass gc = GeologicalClass.builder().geologicalClassId(geologicalUuid).code("GC11").name("Geo Class 11").build();
        ArrayList<GeologicalClass> geologicalClasses = new ArrayList<>() {{
            add(gc);
        }};
        Section section = Section.builder().sectionId(sectionId).name("Section 1").geologicalClasses(geologicalClasses).build();
        sectionRepository.save(section);
        return section;
    }


    @Test
    @SneakyThrows
    void should_report_not_found_when_get_with_section_id_not_exist() {
        //Given
        UUID geologicalUuid = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();
        saveSection(geologicalUuid, sectionId);

        //When
        String requestUrl = SECTION_URL + "/" + UUID.randomUUID();
        MockHttpServletRequestBuilder content = get(requestUrl);
        ResultActions response = mvc.perform(content);

        //Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.statusMessage").value("Can not find the section."));

    }


    @Test
    @SneakyThrows
    void should_update_section_and_geologicals() {
        //Given
        UUID geologicalUuid = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();
        saveSection(geologicalUuid, sectionId);

        GeologicalClassDto gc1 = GeologicalClassDto.builder().geologicalClassId(geologicalUuid).code("GC11").name("Geo Class 22").build();
        ArrayList<GeologicalClassDto> geologicalClasses = new ArrayList<>() {{
            add(gc1);
        }};
        SectionDto sectionDtoRequest = SectionDto.builder().sectionId(sectionId).name("Section 2").geologicalClasses(geologicalClasses).build();


        String sectionJson = mapper.writeValueAsString(sectionDtoRequest);


        //When
        MockHttpServletRequestBuilder content = put(SECTION_URL).contentType(APPLICATION_JSON).content(sectionJson);
        ResultActions response = mvc.perform(content);

        //Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode", is(OK.value())))
                .andExpect(jsonPath("$.response", is(sectionId.toString())));

        String updatedSectionName = sectionRepository.findById(sectionId).get().getName();
        assertEquals(updatedSectionName, "Section 2");

        String updatedGeologicalName = geologicalClassRepository.findById(geologicalUuid).get().getName();
        assertEquals(updatedGeologicalName, "Geo Class 22");

    }

    @Test
    @SneakyThrows
    void should_report_error_when_update_with_blank_section_i() {
        //Given
        SectionDto sectionDtoRequest = buildSectionDtoRequest();
        sectionDtoRequest.setSectionId(null);
        String sectionJson = mapper.writeValueAsString(sectionDtoRequest);


        //When
        MockHttpServletRequestBuilder content = put(SECTION_URL).contentType(APPLICATION_JSON).content(sectionJson);
        ResultActions response = mvc.perform(content);

        //Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode", is(BAD_REQUEST.value())))
                .andExpect(jsonPath("$.statusMessage", is("Section id cannot be null when update the section")));
        ;

    }

    @Test
    @SneakyThrows
    void should_report_error_when_update_with_blank_geological_id() {
        //Given
        SectionDto sectionDtoRequest = buildSectionDtoRequest();
        sectionDtoRequest.setSectionId(UUID.randomUUID());

        String sectionJson = mapper.writeValueAsString(sectionDtoRequest);


        //When
        MockHttpServletRequestBuilder content = put(SECTION_URL).contentType(APPLICATION_JSON).content(sectionJson);
        ResultActions response = mvc.perform(content);

        //Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode", is(BAD_REQUEST.value())))
                .andExpect(jsonPath("$.statusMessage", is("Geological id cannot be null when update the section")));
        ;

    }

    @Test
    @SneakyThrows
    void should_report_not_found_when_update_section_id_not_exist() {
        //Given
        UUID existGeologicalUuid = UUID.randomUUID();
        UUID existSectionId = UUID.randomUUID();
        saveSection(existGeologicalUuid, existSectionId);


        UUID nonExistSectionId = UUID.randomUUID();
        GeologicalClassDto gc1 = GeologicalClassDto.builder().geologicalClassId(existGeologicalUuid).code("GC11").name("Geo Class 11").build();
        ArrayList<GeologicalClassDto> geologicalClasses = new ArrayList<>() {{
            add(gc1);
        }};
        SectionDto sectionDtoRequest = SectionDto.builder().sectionId(nonExistSectionId).name("Section 1").geologicalClasses(geologicalClasses).build();

        String sectionJson = mapper.writeValueAsString(sectionDtoRequest);


        //When
        MockHttpServletRequestBuilder content = put(SECTION_URL).contentType(APPLICATION_JSON).content(sectionJson);
        ResultActions response = mvc.perform(content);

        //Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.statusMessage", is(String.format("Section id: %s do not exist.", nonExistSectionId))));
    }

    @Test
    @SneakyThrows
    void should_report_not_found_when_update_geological_id_not_exist() {
        //Given
        UUID existGeologicalUuid = UUID.randomUUID();
        UUID existSectionId = UUID.randomUUID();
        saveSection(existGeologicalUuid, existSectionId);

        UUID nonExistGeologicalUuid = UUID.randomUUID();
        GeologicalClassDto gc1 = GeologicalClassDto.builder().geologicalClassId(nonExistGeologicalUuid).code("GC11").name("Geo Class 11").build();
        ArrayList<GeologicalClassDto> geologicalClasses = new ArrayList<>() {{
            add(gc1);
        }};
        SectionDto sectionDtoRequest = SectionDto.builder().sectionId(existSectionId).name("Section 1").geologicalClasses(geologicalClasses).build();

        String sectionJson = mapper.writeValueAsString(sectionDtoRequest);


        //When
        MockHttpServletRequestBuilder content = put(SECTION_URL).contentType(APPLICATION_JSON).content(sectionJson);
        ResultActions response = mvc.perform(content);

        //Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.statusMessage", is(String.format("Geological id: %s do not exist.", nonExistGeologicalUuid))));

    }

    @Test
    @SneakyThrows
    void should_delete_section_and_geologicalClass_by_sectionId() {
        //Given
        UUID existGeologicalUuid = UUID.randomUUID();
        UUID existSectionId = UUID.randomUUID();
        saveSection(existGeologicalUuid, existSectionId);

        //When
        String requestUrl = SECTION_URL + "/" + existSectionId;
        MockHttpServletRequestBuilder content = delete(requestUrl);
        ResultActions response = mvc.perform(content);

        //Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode", is(OK.value())))
                .andExpect(jsonPath("$.response", is(existSectionId.toString())));


        assertTrue(sectionRepository.findById(existSectionId).isEmpty());
        assertFalse(geologicalClassRepository.findById(existGeologicalUuid).isEmpty());
    }

    @Test
    @SneakyThrows
    void should_report_not_found_when_delete_by_nonexist_section_id() {
        //Given
        UUID existGeologicalUuid = UUID.randomUUID();
        UUID existSectionId = UUID.randomUUID();
        saveSection(existGeologicalUuid, existSectionId);

        //When
        String requestUrl = SECTION_URL + "/" + UUID.randomUUID();
        MockHttpServletRequestBuilder content = delete(requestUrl);
        ResultActions response = mvc.perform(content);

        //Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.statusMessage", is(String.format("Geological id: %s do not exist.", existSectionId))));
    }

    @Test
    @SneakyThrows
    void should_report_argument_invalid_when_delete_by_null_section_id() {
        //Given
        UUID existGeologicalUuid = UUID.randomUUID();
        UUID existSectionId = UUID.randomUUID();
        saveSection(existGeologicalUuid, existSectionId);

        //When
        String requestUrl = SECTION_URL + "/";
        MockHttpServletRequestBuilder content = delete(requestUrl);
        ResultActions response = mvc.perform(content);

        //Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode", is(BAD_REQUEST.value())))
                .andExpect(jsonPath("$.statusMessage", is("Missing section Id")));
    }

    @Test
    @SneakyThrows
    void should_report_argument_invalid_when_delete_by_invalid_section_id() {
        //Given
        UUID existGeologicalUuid = UUID.randomUUID();
        UUID existSectionId = UUID.randomUUID();
        saveSection(existGeologicalUuid, existSectionId);

        //When
        String requestUrl = SECTION_URL + "/"+"invalid uuid";
        MockHttpServletRequestBuilder content = delete(requestUrl);
        ResultActions response = mvc.perform(content);

        //Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode", is(BAD_REQUEST.value())))
                .andExpect(jsonPath("$.statusMessage", is("Invalid section id")));

    }

}