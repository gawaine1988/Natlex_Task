package org.example.natlex_task.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.example.natlex_task.domain.GeologicalClass;
import org.example.natlex_task.domain.Section;
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
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
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
        GeologicalClass gc1 = GeologicalClass.builder().code("GC11").name("Geo Class 11").build();
        GeologicalClass gc2 = GeologicalClass.builder().code("GC12").name("Geo Class 12").build();
        ArrayList<GeologicalClass> geologicalClasses = new ArrayList<>() {{
            add(gc1);
            add(gc2);
        }};
        Section section = Section.builder().name("Section 1").geologicalClasses(geologicalClasses).build();
        String sectionJson = mapper.writeValueAsString(section);

        //When
        String requestUrl = "http://localhost:8081"+SECTION_PATH;
        MockHttpServletRequestBuilder content = post(requestUrl).contentType(APPLICATION_JSON).content(sectionJson);
        ResultActions response = mvc.perform(content);

        //Then
        response.andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.response", is(notNullValue())));
    }
}