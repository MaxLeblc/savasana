package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.services.SessionService;
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
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for SessionController
 * 
 * Tests all REST endpoints for session management:
 * - GET /api/session/{id} - Get session by ID
 * - GET /api/session - Get all sessions
 * - POST /api/session - Create new session
 * - PUT /api/session/{id} - Update session
 * - DELETE /api/session/{id} - Delete session
 * - POST /api/session/{id}/participate/{userId} - User joins session
 * - DELETE /api/session/{id}/participate/{userId} - User leaves session
 */
@SpringBootTest
@AutoConfigureMockMvc
public class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SessionService sessionService;

    @MockBean
    private SessionMapper sessionMapper;

    // ========== GET /api/session/{id} Tests ==========

    @Test
    @WithMockUser
    public void testFindById_ExistingSession_ReturnsSession() throws Exception {
        // ARRANGE
        Long sessionId = 1L;
        Session session = new Session();
        session.setId(sessionId);
        session.setName("Morning Yoga");
        session.setDescription("Relaxing morning session");

        SessionDto sessionDto = new SessionDto();
        sessionDto.setId(sessionId);
        sessionDto.setName("Morning Yoga");
        sessionDto.setDescription("Relaxing morning session");

        when(sessionService.getById(sessionId)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        // ACT & ASSERT
        mockMvc.perform(get("/api/session/{id}", sessionId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(sessionId))
                .andExpect(jsonPath("$.name").value("Morning Yoga"))
                .andExpect(jsonPath("$.description").value("Relaxing morning session"));
    }

    @Test
    @WithMockUser
    public void testFindById_NonExistingSession_ReturnsNotFound() throws Exception {
        // ARRANGE
        Long sessionId = 999L;
        when(sessionService.getById(sessionId)).thenReturn(null);

        // ACT & ASSERT
        mockMvc.perform(get("/api/session/{id}", sessionId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    public void testFindById_InvalidIdFormat_ReturnsBadRequest() throws Exception {
        // ACT & ASSERT
        mockMvc.perform(get("/api/session/{id}", "invalid"))
                .andExpect(status().isBadRequest());
    }

    // ========== GET /api/session Tests ==========

    @Test
    @WithMockUser
    public void testFindAll_ReturnsAllSessions() throws Exception {
        // ARRANGE
        Session session1 = new Session();
        session1.setId(1L);
        session1.setName("Morning Yoga");

        Session session2 = new Session();
        session2.setId(2L);
        session2.setName("Evening Yoga");

        List<Session> sessions = Arrays.asList(session1, session2);

        SessionDto sessionDto1 = new SessionDto();
        sessionDto1.setId(1L);
        sessionDto1.setName("Morning Yoga");

        SessionDto sessionDto2 = new SessionDto();
        sessionDto2.setId(2L);
        sessionDto2.setName("Evening Yoga");

        List<SessionDto> sessionDtos = Arrays.asList(sessionDto1, sessionDto2);

        when(sessionService.findAll()).thenReturn(sessions);
        when(sessionMapper.toDto(sessions)).thenReturn(sessionDtos);

        // ACT & ASSERT
        mockMvc.perform(get("/api/session"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Morning Yoga"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Evening Yoga"));
    }

    @Test
    @WithMockUser
    public void testFindAll_NoSessions_ReturnsEmptyList() throws Exception {
        // ARRANGE
        when(sessionService.findAll()).thenReturn(Collections.emptyList());
        when(sessionMapper.toDto(Collections.emptyList())).thenReturn(Collections.emptyList());

        // ACT & ASSERT
        mockMvc.perform(get("/api/session"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ========== POST /api/session Tests ==========

    @Test
    @WithMockUser
    public void testCreate_ValidSession_ReturnsCreatedSession() throws Exception {
        // ARRANGE
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("New Yoga Session");
        sessionDto.setDescription("A new session");
        sessionDto.setDate(new Date());
        sessionDto.setTeacher_id(1L);

        Session session = new Session();
        session.setId(1L);
        session.setName("New Yoga Session");

        SessionDto returnedDto = new SessionDto();
        returnedDto.setId(1L);
        returnedDto.setName("New Yoga Session");

        when(sessionMapper.toEntity(any(SessionDto.class))).thenReturn(session);
        when(sessionService.create(any(Session.class))).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(returnedDto);

        // ACT & ASSERT
        mockMvc.perform(post("/api/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Yoga Session"));

        verify(sessionService, times(1)).create(any(Session.class));
    }

    // ========== PUT /api/session/{id} Tests ==========

    @Test
    @WithMockUser
    public void testUpdate_ValidSession_ReturnsUpdatedSession() throws Exception {
        // ARRANGE
        Long sessionId = 1L;
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("Updated Yoga Session");
        sessionDto.setDescription("Updated description");
        sessionDto.setDate(new Date());
        sessionDto.setTeacher_id(1L);

        Session session = new Session();
        session.setId(sessionId);
        session.setName("Updated Yoga Session");

        SessionDto returnedDto = new SessionDto();
        returnedDto.setId(sessionId);
        returnedDto.setName("Updated Yoga Session");

        when(sessionMapper.toEntity(any(SessionDto.class))).thenReturn(session);
        when(sessionService.update(eq(sessionId), any(Session.class))).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(returnedDto);

        // ACT & ASSERT
        mockMvc.perform(put("/api/session/{id}", sessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(sessionId))
                .andExpect(jsonPath("$.name").value("Updated Yoga Session"));

        verify(sessionService, times(1)).update(eq(sessionId), any(Session.class));
    }

    @Test
    @WithMockUser
    public void testUpdate_InvalidIdFormat_ReturnsBadRequest() throws Exception {
        // ARRANGE
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("Updated Session");

        // ACT & ASSERT
        mockMvc.perform(put("/api/session/{id}", "invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isBadRequest());
    }

    // ========== DELETE /api/session/{id} Tests ==========

    @Test
    @WithMockUser
    public void testDelete_ExistingSession_ReturnsOk() throws Exception {
        // ARRANGE
        Long sessionId = 1L;
        Session session = new Session();
        session.setId(sessionId);

        when(sessionService.getById(sessionId)).thenReturn(session);
        doNothing().when(sessionService).delete(sessionId);

        // ACT & ASSERT
        mockMvc.perform(delete("/api/session/{id}", sessionId))
                .andExpect(status().isOk());

        verify(sessionService, times(1)).delete(sessionId);
    }

    @Test
    @WithMockUser
    public void testDelete_NonExistingSession_ReturnsNotFound() throws Exception {
        // ARRANGE
        Long sessionId = 999L;
        when(sessionService.getById(sessionId)).thenReturn(null);

        // ACT & ASSERT
        mockMvc.perform(delete("/api/session/{id}", sessionId))
                .andExpect(status().isNotFound());

        verify(sessionService, never()).delete(anyLong());
    }

    @Test
    @WithMockUser
    public void testDelete_InvalidIdFormat_ReturnsBadRequest() throws Exception {
        // ACT & ASSERT
        mockMvc.perform(delete("/api/session/{id}", "invalid"))
                .andExpect(status().isBadRequest());
    }

    // ========== POST /api/session/{id}/participate/{userId} Tests ==========

    @Test
    @WithMockUser
    public void testParticipate_ValidIds_ReturnsOk() throws Exception {
        // ARRANGE
        Long sessionId = 1L;
        Long userId = 10L;

        doNothing().when(sessionService).participate(sessionId, userId);

        // ACT & ASSERT
        mockMvc.perform(post("/api/session/{id}/participate/{userId}", sessionId, userId))
                .andExpect(status().isOk());

        verify(sessionService, times(1)).participate(sessionId, userId);
    }

    @Test
    @WithMockUser
    public void testParticipate_InvalidSessionIdFormat_ReturnsBadRequest() throws Exception {
        // ACT & ASSERT
        mockMvc.perform(post("/api/session/{id}/participate/{userId}", "invalid", 10L))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    public void testParticipate_InvalidUserIdFormat_ReturnsBadRequest() throws Exception {
        // ACT & ASSERT
        mockMvc.perform(post("/api/session/{id}/participate/{userId}", 1L, "invalid"))
                .andExpect(status().isBadRequest());
    }

    // ========== DELETE /api/session/{id}/participate/{userId} Tests ==========

    @Test
    @WithMockUser
    public void testNoLongerParticipate_ValidIds_ReturnsOk() throws Exception {
        // ARRANGE
        Long sessionId = 1L;
        Long userId = 10L;

        doNothing().when(sessionService).noLongerParticipate(sessionId, userId);

        // ACT & ASSERT
        mockMvc.perform(delete("/api/session/{id}/participate/{userId}", sessionId, userId))
                .andExpect(status().isOk());

        verify(sessionService, times(1)).noLongerParticipate(sessionId, userId);
    }

    @Test
    @WithMockUser
    public void testNoLongerParticipate_InvalidSessionIdFormat_ReturnsBadRequest() throws Exception {
        // ACT & ASSERT
        mockMvc.perform(delete("/api/session/{id}/participate/{userId}", "invalid", 10L))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    public void testNoLongerParticipate_InvalidUserIdFormat_ReturnsBadRequest() throws Exception {
        // ACT & ASSERT
        mockMvc.perform(delete("/api/session/{id}/participate/{userId}", 1L, "invalid"))
                .andExpect(status().isBadRequest());
    }

    // ========== Authentication Tests ==========

    @Test
    public void testFindById_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
        // ACT & ASSERT - No @WithMockUser annotation
        mockMvc.perform(get("/api/session/{id}", 1L))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testCreate_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
        // ARRANGE
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("Test Session");

        // ACT & ASSERT
        mockMvc.perform(post("/api/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isUnauthorized());
    }
}
