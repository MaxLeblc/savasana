package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for UserController
 * 
 * Tests REST endpoints for user management:
 * - GET /api/user/{id} - Get user by ID
 * - DELETE /api/user/{id} - Delete user (with authorization check)
 * 
 * Key security feature: Users can only delete their own account
 * (checked via SecurityContext username matching user email)
 */
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    // ========== GET /api/user/{id} Tests ==========

    /**
     * Test GET /api/user/{id} with valid existing user
     * Expected: 200 OK with user data
     */
    @Test
    @WithMockUser(username = "test@example.com")
    public void testFindById_ExistingUser_ReturnsUser() throws Exception {
        // ARRANGE
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");

        UserDto userDto = new UserDto();
        userDto.setId(userId);
        userDto.setEmail("test@example.com");
        userDto.setFirstName("John");
        userDto.setLastName("Doe");

        when(userService.findById(userId)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        // ACT & ASSERT
        mockMvc.perform(get("/api/user/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    /**
     * Test GET /api/user/{id} with non-existing user
     * Expected: 404 NOT FOUND
     */
    @Test
    @WithMockUser
    public void testFindById_NonExistingUser_ReturnsNotFound() throws Exception {
        // ARRANGE
        Long userId = 999L;
        when(userService.findById(userId)).thenReturn(null);

        // ACT & ASSERT
        mockMvc.perform(get("/api/user/{id}", userId))
                .andExpect(status().isNotFound());
    }

    /**
     * Test GET /api/user/{id} with invalid ID format
     * Expected: 400 BAD REQUEST
     */
    @Test
    @WithMockUser
    public void testFindById_InvalidIdFormat_ReturnsBadRequest() throws Exception {
        // ACT & ASSERT - Test with non-numeric ID
        mockMvc.perform(get("/api/user/{id}", "invalid"))
                .andExpect(status().isBadRequest());
    }

    // ========== DELETE /api/user/{id} Tests ==========

    /**
     * Test DELETE /api/user/{id} when user deletes their own account
     * Expected: 200 OK (authorized deletion)
     */
    @Test
    @WithMockUser(username = "user@example.com")
    public void testDelete_OwnAccount_ReturnsOk() throws Exception {
        // ARRANGE
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail("user@example.com");  // Matches authenticated username

        when(userService.findById(userId)).thenReturn(user);
        doNothing().when(userService).delete(userId);

        // ACT & ASSERT
        mockMvc.perform(delete("/api/user/{id}", userId))
                .andExpect(status().isOk());

        verify(userService, times(1)).delete(userId);
    }

    /**
     * Test DELETE /api/user/{id} when user tries to delete another user's account
     * Expected: 401 UNAUTHORIZED (authorization check fails)
     */
    @Test
    @WithMockUser(username = "attacker@example.com")
    public void testDelete_OtherUserAccount_ReturnsUnauthorized() throws Exception {
        // ARRANGE
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail("victim@example.com");  // Different from authenticated username

        when(userService.findById(userId)).thenReturn(user);

        // ACT & ASSERT
        mockMvc.perform(delete("/api/user/{id}", userId))
                .andExpect(status().isUnauthorized());

        // Verify delete was never called due to authorization failure
        verify(userService, never()).delete(anyLong());
    }

    /**
     * Test DELETE /api/user/{id} with non-existing user
     * Expected: 404 NOT FOUND
     */
    @Test
    @WithMockUser(username = "user@example.com")
    public void testDelete_NonExistingUser_ReturnsNotFound() throws Exception {
        // ARRANGE
        Long userId = 999L;
        when(userService.findById(userId)).thenReturn(null);

        // ACT & ASSERT
        mockMvc.perform(delete("/api/user/{id}", userId))
                .andExpect(status().isNotFound());

        verify(userService, never()).delete(anyLong());
    }

    /**
     * Test DELETE /api/user/{id} with invalid ID format
     * Expected: 400 BAD REQUEST
     */
    @Test
    @WithMockUser
    public void testDelete_InvalidIdFormat_ReturnsBadRequest() throws Exception {
        // ACT & ASSERT
        mockMvc.perform(delete("/api/user/{id}", "invalid"))
                .andExpect(status().isBadRequest());
    }

    // ========== Authentication Tests ==========

    /**
     * Test accessing user endpoints without authentication
     * Expected: 401 UNAUTHORIZED
     */
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

    // ========== Edge Cases ==========

    /**
     * Test DELETE with valid format but user exists with different email
     * This tests the authorization logic specifically
     */
    @Test
    @WithMockUser(username = "john@example.com")
    public void testDelete_AuthorizationCheck_VerifiesEmailMatch() throws Exception {
        // ARRANGE
        Long userId = 5L;
        User user = new User();
        user.setId(userId);
        user.setEmail("jane@example.com");  // Different email

        when(userService.findById(userId)).thenReturn(user);

        // ACT & ASSERT
        mockMvc.perform(delete("/api/user/{id}", userId))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).delete(anyLong());
    }

    /**
     * Test successful deletion verifies the full flow:
     * 1. User exists
     * 2. Email matches authenticated user
     * 3. Delete is called
     */
    @Test
    @WithMockUser(username = "admin@yoga.com")
    public void testDelete_CompleteSuccessFlow() throws Exception {
        // ARRANGE
        Long userId = 10L;
        User user = new User();
        user.setId(userId);
        user.setEmail("admin@yoga.com");  // Exact match with authenticated user

        when(userService.findById(userId)).thenReturn(user);
        doNothing().when(userService).delete(userId);

        // ACT & ASSERT
        mockMvc.perform(delete("/api/user/{id}", userId))
                .andExpect(status().isOk());

        // Verify the complete flow
        verify(userService, times(1)).findById(userId);
        verify(userService, times(1)).delete(userId);
    }
}
