package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.TeacherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for TeacherController
 *
 * Uses MockMvc to simulate HTTP requests without starting a full server.
 * @SpringBootTest loads the complete application context.
 * @AutoConfigureMockMvc configures MockMvc for testing REST controllers.
 * @MockBean replaces real beans with mocks in the Spring context.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class TeacherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TeacherService teacherService;

    @MockBean
    private TeacherMapper teacherMapper;

    /**
     * Test GET /api/teacher/{id} with valid existing teacher
     * Expected: 200 OK with teacher data
     */
    @Test
    @WithMockUser  // Simulates an authenticated user
    public void testFindById_ExistingTeacher_ReturnsTeacher() throws Exception {
        // ARRANGE
        Long teacherId = 1L;
        Teacher teacher = new Teacher();
        teacher.setId(teacherId);
        teacher.setFirstName("John");
        teacher.setLastName("Doe");

        TeacherDto teacherDto = new TeacherDto();
        teacherDto.setId(teacherId);
        teacherDto.setFirstName("John");
        teacherDto.setLastName("Doe");

        when(teacherService.findById(teacherId)).thenReturn(teacher);
        when(teacherMapper.toDto(teacher)).thenReturn(teacherDto);

        // ACT & ASSERT
        mockMvc.perform(get("/api/teacher/{id}", teacherId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(teacherId))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    /**
     * Test GET /api/teacher/{id} with non-existing teacher
     * Expected: 404 NOT FOUND
     */
    @Test
    @WithMockUser
    public void testFindById_NonExistingTeacher_ReturnsNotFound() throws Exception {
        // ARRANGE
        Long teacherId = 999L;
        when(teacherService.findById(teacherId)).thenReturn(null);

        // ACT & ASSERT
        mockMvc.perform(get("/api/teacher/{id}", teacherId))
                .andExpect(status().isNotFound());
    }

    /**
     * Test GET /api/teacher/{id} with invalid ID format
     * Expected: 400 BAD REQUEST
     */
    @Test
    @WithMockUser
    public void testFindById_InvalidIdFormat_ReturnsBadRequest() throws Exception {
        // ACT & ASSERT - Test with non-numeric ID
        mockMvc.perform(get("/api/teacher/{id}", "invalid"))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test GET /api/teacher to retrieve all teachers
     * Expected: 200 OK with list of teachers
     */
    @Test
    @WithMockUser
    public void testFindAll_ReturnsAllTeachers() throws Exception {
        // ARRANGE
        Teacher teacher1 = new Teacher();
        teacher1.setId(1L);
        teacher1.setFirstName("John");
        teacher1.setLastName("Doe");

        Teacher teacher2 = new Teacher();
        teacher2.setId(2L);
        teacher2.setFirstName("Jane");
        teacher2.setLastName("Smith");

        List<Teacher> teachers = Arrays.asList(teacher1, teacher2);

        TeacherDto teacherDto1 = new TeacherDto();
        teacherDto1.setId(1L);
        teacherDto1.setFirstName("John");
        teacherDto1.setLastName("Doe");

        TeacherDto teacherDto2 = new TeacherDto();
        teacherDto2.setId(2L);
        teacherDto2.setFirstName("Jane");
        teacherDto2.setLastName("Smith");

        List<TeacherDto> teacherDtos = Arrays.asList(teacherDto1, teacherDto2);

        when(teacherService.findAll()).thenReturn(teachers);
        when(teacherMapper.toDto(teachers)).thenReturn(teacherDtos);

        // ACT & ASSERT
        mockMvc.perform(get("/api/teacher"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].firstName").value("Jane"));
    }

    /**
     * Test GET /api/teacher when no teachers exist
     * Expected: 200 OK with empty list
     */
    @Test
    @WithMockUser
    public void testFindAll_NoTeachers_ReturnsEmptyList() throws Exception {
        // ARRANGE
        when(teacherService.findAll()).thenReturn(Collections.emptyList());
        when(teacherMapper.toDto(Collections.emptyList())).thenReturn(Collections.emptyList());

        // ACT & ASSERT
        mockMvc.perform(get("/api/teacher"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    /**
     * Test accessing teacher endpoints without authentication
     * Expected: 401 UNAUTHORIZED (depends on security configuration)
     */
    @Test
    public void testFindById_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
        // ACT & ASSERT - No @WithMockUser annotation
        mockMvc.perform(get("/api/teacher/{id}", 1L))
                .andExpect(status().isUnauthorized());
    }
}
