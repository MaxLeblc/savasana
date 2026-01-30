package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.SessionDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for SessionController using H2 database
 * 
 * Test data from data.sql:
 * - Session ID=1: Beginners Yoga (teacher_id=1)
 * - Session ID=2: Advanced Yoga (teacher_id=2)
 * - Teacher ID=1: Margot DELAHAYE
 * - Teacher ID=2: Hélène THIERCELIN
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ========== GET /api/session/{id} Tests ==========

    @Test
    @WithMockUser
    public void testFindById_ExistingSession_ReturnsSession() throws Exception {
        // ACT & ASSERT - Using real data from H2 (Session ID=1: Beginners Yoga)
        mockMvc.perform(get("/api/session/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Beginners Yoga"))
                .andExpect(jsonPath("$.teacher_id").value(1));
    }

    @Test
    @WithMockUser
    public void testFindById_NonExistingSession_ReturnsNotFound() throws Exception {
        // ACT & ASSERT - ID 999 doesn't exist in H2
        mockMvc.perform(get("/api/session/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    public void testFindById_InvalidIdFormat_ReturnsBadRequest() throws Exception {
        // ACT & ASSERT - Invalid ID format
        mockMvc.perform(get("/api/session/{id}", "invalid"))
                .andExpect(status().isBadRequest());
    }

    // ========== GET /api/session Tests ==========

    @Test
    @WithMockUser
    public void testFindAll_ReturnsAllSessions() throws Exception {
        // ACT & ASSERT - Using real data from H2 (2 sessions)
        mockMvc.perform(get("/api/session"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Beginners Yoga"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Advanced Yoga"));
    }

    // ========== POST /api/session Tests ==========

    @Test
    @WithMockUser
    public void testCreate_ValidSession_ReturnsCreatedSession() throws Exception {
        // ARRANGE - Create a new session with real teacher ID
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("Evening Yoga");
        sessionDto.setDescription("Relaxing evening session");
        sessionDto.setDate(new Date());
        sessionDto.setTeacher_id(1L); // Teacher Margot DELAHAYE

        // ACT & ASSERT
        mockMvc.perform(post("/api/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Evening Yoga"))
                .andExpect(jsonPath("$.teacher_id").value(1));
    }

    // ========== PUT /api/session/{id} Tests ==========

    @Test
    @WithMockUser
    public void testUpdate_ValidSession_ReturnsUpdatedSession() throws Exception {
        // ARRANGE - Update existing session ID=1
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("Updated Beginners Yoga");
        sessionDto.setDescription("Updated description");
        sessionDto.setDate(new Date());
        sessionDto.setTeacher_id(2L); // Change teacher to Hélène THIERCELIN

        // ACT & ASSERT
        mockMvc.perform(put("/api/session/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Beginners Yoga"))
                .andExpect(jsonPath("$.teacher_id").value(2));
    }

    @Test
    @WithMockUser
    public void testUpdate_InvalidIdFormat_ReturnsBadRequest() throws Exception {
        // ARRANGE
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("Test");
        sessionDto.setDate(new Date());
        sessionDto.setTeacher_id(1L);

        // ACT & ASSERT - Invalid ID format
        mockMvc.perform(put("/api/session/{id}", "invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isBadRequest());
    }

    // ========== DELETE /api/session/{id} Tests ==========

    @Test
    @WithMockUser
    public void testDelete_ExistingSession_ReturnsOk() throws Exception {
        // ACT & ASSERT - Delete session ID=2 (Advanced Yoga)
        mockMvc.perform(delete("/api/session/{id}", 2L))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void testDelete_NonExistingSession_ReturnsNotFound() throws Exception {
        // ACT & ASSERT - Delete non-existing session
        mockMvc.perform(delete("/api/session/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    public void testDelete_InvalidIdFormat_ReturnsBadRequest() throws Exception {
        // ACT & ASSERT - Invalid ID format
        mockMvc.perform(delete("/api/session/{id}", "invalid"))
                .andExpect(status().isBadRequest());
    }

    // ========== POST /api/session/{id}/participate/{userId} Tests ==========

    @Test
    @WithMockUser
    public void testParticipate_ValidSessionAndUser_ReturnsOk() throws Exception {
        // ACT & ASSERT - User ID=2 joins Session ID=1
        mockMvc.perform(post("/api/session/{id}/participate/{userId}", 1L, 2L))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void testParticipate_NonExistingSession_ReturnsNotFound() throws Exception {
        // ACT & ASSERT - Non-existing session
        mockMvc.perform(post("/api/session/{id}/participate/{userId}", 999L, 2L))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    public void testParticipate_NonExistingUser_ReturnsBadRequest() throws Exception {
        // ACT & ASSERT - Non-existing user
        mockMvc.perform(post("/api/session/{id}/participate/{userId}", 1L, 999L))
                .andExpect(status().isNotFound());
    }

    // ========== DELETE /api/session/{id}/participate/{userId} Tests ==========

    @Test
    @WithMockUser
    public void testNoLongerParticipate_ValidSessionAndUser_ReturnsOk() throws Exception {
        // First participate
        mockMvc.perform(post("/api/session/{id}/participate/{userId}", 1L, 2L))
                .andExpect(status().isOk());

        // Then leave
        mockMvc.perform(delete("/api/session/{id}/participate/{userId}", 1L, 2L))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void testNoLongerParticipate_NonExistingSession_ReturnsNotFound() throws Exception {
        // ACT & ASSERT
        mockMvc.perform(delete("/api/session/{id}/participate/{userId}", 999L, 2L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testFindById_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
        // ACT & ASSERT - No @WithMockUser annotation
        mockMvc.perform(get("/api/session/{id}", 1L))
                .andExpect(status().isUnauthorized());
    }
}
