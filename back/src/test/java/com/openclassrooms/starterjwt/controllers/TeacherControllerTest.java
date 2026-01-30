package com.openclassrooms.starterjwt.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for TeacherController with H2 database
 *
 * Tests use real database with data from data.sql:
 * - Teacher ID=1: Margot DELAHAYE
 * - Teacher ID=2: Hélène THIERCELIN
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TeacherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Test GET /api/teacher/{id} with existing teacher from H2
     * Teacher ID=1 is Margot DELAHAYE (from data.sql)
     */
    @Test
    @WithMockUser
    public void testFindById_ExistingTeacher_ReturnsTeacher() throws Exception {
        // ACT & ASSERT - Using real data from H2
        mockMvc.perform(get("/api/teacher/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Margot"))
                .andExpect(jsonPath("$.lastName").value("DELAHAYE"));
    }

    /**
     * Test GET /api/teacher/{id} with non-existing teacher
     */
    @Test
    @WithMockUser
    public void testFindById_NonExistingTeacher_ReturnsNotFound() throws Exception {
        // ACT & ASSERT - ID 999 doesn't exist in H2
        mockMvc.perform(get("/api/teacher/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    /**
     * Test GET /api/teacher/{id} with Teacher ID=2 (Hélène THIERCELIN)
     */
    @Test
    @WithMockUser
    public void testFindById_SecondTeacher_ReturnsTeacher() throws Exception {
        // ACT & ASSERT - Using real data from H2
        mockMvc.perform(get("/api/teacher/{id}", 2L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.firstName").value("Hélène"))
                .andExpect(jsonPath("$.lastName").value("THIERCELIN"));
    }

    /**
     * Test GET /api/teacher/{id} with invalid ID format
     */
    @Test
    @WithMockUser
    public void testFindById_InvalidIdFormat_ReturnsBadRequest() throws Exception {
        // ACT & ASSERT - Test with non-numeric ID
        mockMvc.perform(get("/api/teacher/{id}", "invalid"))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test GET /api/teacher to retrieve all teachers from H2
     * Expected: 2 teachers (Margot DELAHAYE, Hélène THIERCELIN)
     */
    @Test
    @WithMockUser
    public void testFindAll_ReturnsAllTeachers() throws Exception {
        // ACT & ASSERT - Using real data from H2
        mockMvc.perform(get("/api/teacher"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("Margot"))
                .andExpect(jsonPath("$[0].lastName").value("DELAHAYE"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].firstName").value("Hélène"))
                .andExpect(jsonPath("$[1].lastName").value("THIERCELIN"));
    }

    /**
     * Test accessing teacher endpoints without authentication
     */
    @Test
    public void testFindById_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
        // ACT & ASSERT - No @WithMockUser annotation
        mockMvc.perform(get("/api/teacher/{id}", 1L))
                .andExpect(status().isUnauthorized());
    }
}
