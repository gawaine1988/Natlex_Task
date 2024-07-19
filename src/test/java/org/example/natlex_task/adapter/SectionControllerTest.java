package org.example.natlex_task.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.example.natlex_task.adapter.dto.GeologicalClassDto;
import org.example.natlex_task.adapter.dto.SectionDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

}