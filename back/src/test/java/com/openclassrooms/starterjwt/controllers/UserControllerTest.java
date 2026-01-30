package com.openclassrooms.starterjwt.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for UserController using H2 database
 * 
 * Test data from data.sql:
 * - User ID=1: yoga@studio.com (admin=true)
 * - User ID=2: user@test.com (admin=false)
 * 
 * Key security feature: Users can only delete their own account
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // ========== GET /api/user/{id} Tests ==========

    @Test
    @WithMockUser(username = "yoga@studio.com")
    public void testFindById_ExistingUser_ReturnsUser() throws Exception {
        // ACT & ASSERT - Using real data from H2 (User ID=1: yoga@studio.com)
        mockMvc.perform(get("/api/user/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("yoga@studio.com"))
                .andExpect(jsonPath("$.firstName").value("Admin"))
                .andExpect(jsonPath("$.lastName").value("Admin"))
                .andExpect(jsonPath("$.admin").value(true));
    }

    @Test
    @WithMockUser(username = "user@test.com")
    public void testFindById_NonExistingUser_ReturnsNotFound() throws Exception {
        // ACT & ASSERT - ID 999 doesn't exist in H2
        mockMvc.perform(get("/api/user/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    public void testFindById_InvalidIdFormat_ReturnsBadRequest() throws Exception {
        // ACT & ASSERT - Invalid ID format
        mockMvc.perform(get("/api/user/{id}", "invalid"))
                .andExpect(status().isBadRequest());
    }

    // ========== DELETE /api/user/{id} Tests ==========

    @Test
    @WithMockUser(username = "user@test.com")
    public void testDelete_OwnAccount_ReturnsOk() throws Exception {
        // ACT & ASSERT - User ID=2 (user@test.com) deleting own account
        mockMvc.perform(delete("/api/user/{id}", 2L))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user@test.com")
    public void testDelete_OtherUserAccount_ReturnsUnauthorized() throws Exception {
        // ACT & ASSERT - User ID=2 trying to delete User ID=1 (not allowed)
        mockMvc.perform(delete("/api/user/{id}", 1L))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "yoga@studio.com")
    public void testDelete_NonExistingUser_ReturnsNotFound() throws Exception {
        // ACT & ASSERT - Non-existing user
        mockMvc.perform(delete("/api/user/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    public void testDelete_InvalidIdFormat_ReturnsBadRequest() throws Exception {
        // ACT & ASSERT - Invalid ID format
        mockMvc.perform(delete("/api/user/{id}", "invalid"))
                .andExpect(status().isBadRequest());
    }

    // ========== Authentication Tests ==========

    @Test
    public void testFindById_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
        // ACT & ASSERT - No @WithMockUser annotation
        mockMvc.perform(get("/api/user/{id}", 1L))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testDelete_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
        // ACT & ASSERT
        mockMvc.perform(delete("/api/user/{id}", 1L))
                .andExpect(status().isUnauthorized());
    }
}
