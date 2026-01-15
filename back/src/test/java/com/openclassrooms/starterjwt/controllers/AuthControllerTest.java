package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for AuthController
 * 
 * Tests the authentication and registration endpoints
 * Covers successful scenarios, validation errors, and business logic failures
 */
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserRepository userRepository;

    // ========================================
    // POST /api/auth/login - Login Tests
    // ========================================

    @Test
    @DisplayName("Should authenticate user and return JWT token when credentials are valid")
    public void testLogin_ValidCredentials_ReturnsJwtToken() throws Exception {
        // ARRANGE: Prepare test data for successful login
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("user@example.com");
        loginRequest.setPassword("password123");

        // Create a mock authenticated user
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("encodedPassword");
        user.setAdmin(false);

        // Create UserDetailsImpl for authentication principal
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("user@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("encodedPassword")
                .build();

        // Mock authentication process
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        // Mock JWT token generation
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("mocked-jwt-token");

        // Mock user repository to return user for admin check
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        // ACT: Send POST request to login endpoint
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                // ASSERT: Verify the response
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"))
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("user@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.admin").value(false));

        // Verify that authentication was attempted
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils, times(1)).generateJwtToken(authentication);
    }

    @Test
    @DisplayName("Should return JWT token with admin flag true when user is admin")
    public void testLogin_AdminUser_ReturnsJwtTokenWithAdminTrue() throws Exception {
        // ARRANGE: Prepare test data for admin user login
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("admin@example.com");
        loginRequest.setPassword("adminPassword");

        // Create a mock admin user
        User adminUser = new User();
        adminUser.setId(2L);
        adminUser.setEmail("admin@example.com");
        adminUser.setFirstName("Admin");
        adminUser.setLastName("User");
        adminUser.setPassword("encodedPassword");
        adminUser.setAdmin(true);

        // Create UserDetailsImpl for authentication principal
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(2L)
                .username("admin@example.com")
                .firstName("Admin")
                .lastName("User")
                .password("encodedPassword")
                .build();

        // Mock authentication process
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        // Mock JWT token generation
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("admin-jwt-token");

        // Mock user repository to return admin user
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));

        // ACT & ASSERT
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("admin-jwt-token"))
                .andExpect(jsonPath("$.admin").value(true));
    }

    @Test
    @DisplayName("Should return 401 when credentials are invalid")
    public void testLogin_InvalidCredentials_ReturnsUnauthorized() throws Exception {
        // ARRANGE: Prepare test data with invalid credentials
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("user@example.com");
        loginRequest.setPassword("wrongPassword");

        // Mock authentication to throw BadCredentialsException
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // ACT & ASSERT: Expect 401 Unauthorized
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());

        // Verify that authentication was attempted
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("Should return 400 when login request has empty email")
    public void testLogin_EmptyEmail_ReturnsBadRequest() throws Exception {
        // ARRANGE: Create request with empty email
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("");
        loginRequest.setPassword("password123");

        // ACT & ASSERT: Expect 400 Bad Request due to validation failure
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());

        // Verify that authentication was NOT attempted
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    @DisplayName("Should return 400 when login request has empty password")
    public void testLogin_EmptyPassword_ReturnsBadRequest() throws Exception {
        // ARRANGE: Create request with empty password
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("user@example.com");
        loginRequest.setPassword("");

        // ACT & ASSERT: Expect 400 Bad Request due to validation failure
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());

        // Verify that authentication was NOT attempted
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    @DisplayName("Should handle case when user not found in repository but authentication succeeds")
    public void testLogin_UserNotFoundInRepository_ReturnsJwtTokenWithAdminFalse() throws Exception {
        // ARRANGE: This edge case tests when authentication succeeds but user lookup fails
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("user@example.com");
        loginRequest.setPassword("password123");

        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("user@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("encodedPassword")
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("jwt-token");

        // User NOT found in repository
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        // ACT & ASSERT: Should still return JWT but admin will be false by default
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.admin").value(false));
    }

    // ========================================
    // POST /api/auth/register - Register Tests
    // ========================================

    @Test
    @DisplayName("Should register new user successfully when email is not taken")
    public void testRegister_NewUser_ReturnsSuccessMessage() throws Exception {
        // ARRANGE: Prepare signup request
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("newuser@example.com");
        signupRequest.setFirstName("Jane");
        signupRequest.setLastName("Smith");
        signupRequest.setPassword("password123");

        // Mock that email is not taken
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);

        // Mock password encoding
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        // Mock user save
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ACT: Send POST request to register endpoint
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                // ASSERT: Verify successful registration
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));

        // Verify that user was saved
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode("password123");
    }

    @Test
    @DisplayName("Should return 400 when email is already taken")
    public void testRegister_EmailAlreadyExists_ReturnsBadRequest() throws Exception {
        // ARRANGE: Prepare signup request with existing email
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("existing@example.com");
        signupRequest.setFirstName("John");
        signupRequest.setLastName("Doe");
        signupRequest.setPassword("password123");

        // Mock that email is already taken
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // ACT & ASSERT: Expect 400 Bad Request with error message
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Email is already taken!"));

        // Verify that user was NOT saved
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should return 400 when email format is invalid")
    public void testRegister_InvalidEmailFormat_ReturnsBadRequest() throws Exception {
        // ARRANGE: Create request with invalid email format
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("invalid-email");
        signupRequest.setFirstName("John");
        signupRequest.setLastName("Doe");
        signupRequest.setPassword("password123");

        // ACT & ASSERT: Expect 400 Bad Request due to @Email validation
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest());

        // Verify that user was NOT saved
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should return 400 when firstName is too short")
    public void testRegister_FirstNameTooShort_ReturnsBadRequest() throws Exception {
        // ARRANGE: Create request with firstName less than 3 characters
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("user@example.com");
        signupRequest.setFirstName("Jo");  // Too short, minimum is 3
        signupRequest.setLastName("Doe");
        signupRequest.setPassword("password123");

        // ACT & ASSERT: Expect 400 Bad Request due to @Size validation
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should return 400 when lastName is too short")
    public void testRegister_LastNameTooShort_ReturnsBadRequest() throws Exception {
        // ARRANGE: Create request with lastName less than 3 characters
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("user@example.com");
        signupRequest.setFirstName("John");
        signupRequest.setLastName("Do");  // Too short, minimum is 3
        signupRequest.setPassword("password123");

        // ACT & ASSERT: Expect 400 Bad Request due to @Size validation
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should return 400 when password is too short")
    public void testRegister_PasswordTooShort_ReturnsBadRequest() throws Exception {
        // ARRANGE: Create request with password less than 6 characters
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("user@example.com");
        signupRequest.setFirstName("John");
        signupRequest.setLastName("Doe");
        signupRequest.setPassword("12345");  // Too short, minimum is 6

        // ACT & ASSERT: Expect 400 Bad Request due to @Size validation
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should return 400 when email exceeds maximum length")
    public void testRegister_EmailTooLong_ReturnsBadRequest() throws Exception {
        // ARRANGE: Create request with email exceeding 50 characters
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("a".repeat(50) + "@example.com");  // Exceeds 50 characters
        signupRequest.setFirstName("John");
        signupRequest.setLastName("Doe");
        signupRequest.setPassword("password123");

        // ACT & ASSERT: Expect 400 Bad Request due to @Size validation
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should return 400 when required fields are missing")
    public void testRegister_MissingFields_ReturnsBadRequest() throws Exception {
        // ARRANGE: Create request with null values (will be missing in JSON)
        SignupRequest signupRequest = new SignupRequest();
        // All fields are null

        // ACT & ASSERT: Expect 400 Bad Request due to @NotBlank validation
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should create user with admin flag set to false by default")
    public void testRegister_NewUser_AdminFlagIsFalse() throws Exception {
        // ARRANGE: Prepare signup request
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("newuser@example.com");
        signupRequest.setFirstName("Jane");
        signupRequest.setLastName("Smith");
        signupRequest.setPassword("password123");

        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        // Capture the saved user to verify admin flag
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            // Verify that admin flag is false
            assert !savedUser.isAdmin() : "User should not be admin by default";
            return savedUser;
        });

        // ACT & ASSERT
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));

        verify(userRepository, times(1)).save(any(User.class));
    }
}
