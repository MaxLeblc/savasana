package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SessionService
 * 
 * This file demonstrates how to test a more complex service with:
 * - Multiple dependencies (SessionRepository AND UserRepository)
 * - Custom exception handling (NotFoundException, BadRequestException)
 * - Business logic (participation verification, adding/removing users)
 */
@ExtendWith(MockitoExtension.class)
public class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SessionService sessionService;

    // ========== Tests for CREATE ==========
    
    @Test
    public void testCreate_SavesSession() {
        // ARRANGE - Prepare a session
        Session session = new Session();
        session.setId(1L);
        session.setName("Yoga session");
        
        // Configure the mock: when save() is called, return the session
        when(sessionRepository.save(any(Session.class))).thenReturn(session);
        
        // ACT - Call the method to test
        Session result = sessionService.create(session);
        
        // ASSERT - Verify the results
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Yoga session", result.getName());
        verify(sessionRepository, times(1)).save(session);
    }

    // ========== Tests for DELETE ==========
    
    @Test
    public void testDelete_CallsRepositoryDeleteById() {
        // ARRANGE
        Long sessionId = 1L;
        
        // No need for when() since deleteById() is void
        doNothing().when(sessionRepository).deleteById(sessionId);
        
        // ACT
        sessionService.delete(sessionId);
        
        // ASSERT - Verify that deleteById was called exactly once
        verify(sessionRepository, times(1)).deleteById(sessionId);
    }

    // ========== Tests for FIND_ALL ==========
    
    @Test
    public void testFindAll_ReturnsAllSessions() {
        // ARRANGE - Create a list of sessions
        Session session1 = new Session();
        session1.setId(1L);
        session1.setName("Morning Yoga");
        
        Session session2 = new Session();
        session2.setId(2L);
        session2.setName("Evening Yoga");
        
        List<Session> sessions = Arrays.asList(session1, session2);
        
        when(sessionRepository.findAll()).thenReturn(sessions);
        
        // ACT
        List<Session> result = sessionService.findAll();
        
        // ASSERT
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Morning Yoga", result.get(0).getName());
        assertEquals("Evening Yoga", result.get(1).getName());
        verify(sessionRepository, times(1)).findAll();
    }

    @Test
    public void testFindAll_ReturnsEmptyList_WhenNoSessions() {
        // ARRANGE - Empty list
        when(sessionRepository.findAll()).thenReturn(new ArrayList<>());
        
        // ACT
        List<Session> result = sessionService.findAll();
        
        // ASSERT
        assertNotNull(result);
        assertEquals(0, result.size());
        assertTrue(result.isEmpty());
    }

    // ========== Tests for GET_BY_ID ==========
    
    @Test
    public void testGetById_SessionExists_ReturnsSession() {
        // ARRANGE
        Session session = new Session();
        session.setId(1L);
        session.setName("Test Session");
        
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        
        // ACT
        Session result = sessionService.getById(1L);
        
        // ASSERT
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Session", result.getName());
    }

    @Test
    public void testGetById_SessionNotExists_ReturnsNull() {
        // ARRANGE - findById returns Optional.empty()
        when(sessionRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        // ACT
        Session result = sessionService.getById(999L);
        
        // ASSERT
        assertNull(result);
    }

    // ========== Tests for UPDATE ==========
    
    @Test
    public void testUpdate_UpdatesSessionWithGivenId() {
        // ARRANGE
        Long sessionId = 1L;
        Session session = new Session();
        session.setName("Updated Session");
        
        Session savedSession = new Session();
        savedSession.setId(sessionId);
        savedSession.setName("Updated Session");
        
        when(sessionRepository.save(any(Session.class))).thenReturn(savedSession);
        
        // ACT
        Session result = sessionService.update(sessionId, session);
        
        // ASSERT - Verify that ID was assigned before save()
        assertNotNull(result);
        assertEquals(sessionId, result.getId());
        assertEquals("Updated Session", result.getName());
        
        // Verify that save() was called with a session having the correct ID
        verify(sessionRepository, times(1)).save(argThat(s -> s.getId().equals(sessionId)));
    }

    // ========== Tests for PARTICIPATE ==========
    
    /**
     * Test nominal case: a user registers for a session
     * 
     * Business logic reminder:
     * 1. Verify that session and user exist
     * 2. Verify that the user is not already participating
     * 3. Add the user to the participants list
     * 4. Save the session
     */
    @Test
    public void testParticipate_UserNotYetParticipating_AddsUser() {
        // ARRANGE
        Long sessionId = 1L;
        Long userId = 10L;
        
        // Create a session with an empty participants list
        Session session = new Session();
        session.setId(sessionId);
        session.setUsers(new ArrayList<>()); // Empty list = nobody participating
        
        User user = new User();
        user.setId(userId);
        user.setEmail("user@test.com");
        
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(sessionRepository.save(any(Session.class))).thenReturn(session);
        
        // ACT
        sessionService.participate(sessionId, userId);
        
        // ASSERT
        // Verify that the user was added to the list
        assertEquals(1, session.getUsers().size());
        assertEquals(userId, session.getUsers().get(0).getId());
        
        // Verify that save() was called
        verify(sessionRepository, times(1)).save(session);
    }

    /**
     * Exception test: non-existing session
     * 
     * The assertThrows() method allows verifying that an exception is thrown.
     * This is an important pattern for testing error cases.
     */
    @Test
    public void testParticipate_SessionNotFound_ThrowsNotFoundException() {
        // ARRANGE
        when(sessionRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        
        // ACT & ASSERT - Verify that the exception is thrown
        assertThrows(NotFoundException.class, () -> {
            sessionService.participate(1L, 10L);
        });
        
        // Verify that save() was never called (exception interrupts processing)
        verify(sessionRepository, never()).save(any());
    }

    @Test
    public void testParticipate_UserNotFound_ThrowsNotFoundException() {
        // ARRANGE
        Session session = new Session();
        when(sessionRepository.findById(anyLong())).thenReturn(Optional.of(session));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        // ACT & ASSERT
        assertThrows(NotFoundException.class, () -> {
            sessionService.participate(1L, 10L);
        });
        
        verify(sessionRepository, never()).save(any());
    }

    /**
     * Business case test: user already registered
     * 
     * Scenario: a user attempts to register twice for the same session.
     * Expected result: BadRequestException
     */
    @Test
    public void testParticipate_UserAlreadyParticipating_ThrowsBadRequestException() {
        // ARRANGE
        Long sessionId = 1L;
        Long userId = 10L;
        
        User user = new User();
        user.setId(userId);
        
        Session session = new Session();
        session.setId(sessionId);
        // User is already in the list!
        session.setUsers(new ArrayList<>(Arrays.asList(user)));
        
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        
        // ACT & ASSERT
        assertThrows(BadRequestException.class, () -> {
            sessionService.participate(sessionId, userId);
        });
        
        // Verify that save() was not called (exception thrown before)
        verify(sessionRepository, never()).save(any());
    }

    // ========== Tests for NO_LONGER_PARTICIPATE ==========
    
    @Test
    public void testNoLongerParticipate_UserParticipating_RemovesUser() {
        // ARRANGE
        Long sessionId = 1L;
        Long userId = 10L;
        
        User user = new User();
        user.setId(userId);
        
        Session session = new Session();
        session.setId(sessionId);
        // User is already participating
        session.setUsers(new ArrayList<>(Arrays.asList(user)));
        
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(sessionRepository.save(any(Session.class))).thenReturn(session);
        
        // ACT
        sessionService.noLongerParticipate(sessionId, userId);
        
        // ASSERT - User was removed from the list
        assertEquals(0, session.getUsers().size());
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    public void testNoLongerParticipate_SessionNotFound_ThrowsNotFoundException() {
        // ARRANGE
        when(sessionRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        // ACT & ASSERT
        assertThrows(NotFoundException.class, () -> {
            sessionService.noLongerParticipate(1L, 10L);
        });
    }

    @Test
    public void testNoLongerParticipate_UserNotParticipating_ThrowsBadRequestException() {
        // ARRANGE
        Long sessionId = 1L;
        Long userId = 10L;
        
        Session session = new Session();
        session.setId(sessionId);
        // Empty list = user is not participating
        session.setUsers(new ArrayList<>());
        
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        
        // ACT & ASSERT
        assertThrows(BadRequestException.class, () -> {
            sessionService.noLongerParticipate(sessionId, userId);
        });
        
        verify(sessionRepository, never()).save(any());
    }

    /**
     * Edge case test: remove a user among several
     * 
     * Verifies that only the target user is removed, others remain.
     */
    @Test
    public void testNoLongerParticipate_MultipleUsers_RemovesOnlyTargetUser() {
        // ARRANGE
        Long sessionId = 1L;
        Long userId1 = 10L;
        Long userId2 = 20L;
        Long userId3 = 30L;
        
        User user1 = new User();
        user1.setId(userId1);
        
        User user2 = new User();
        user2.setId(userId2);
        
        User user3 = new User();
        user3.setId(userId3);
        
        Session session = new Session();
        session.setId(sessionId);
        session.setUsers(new ArrayList<>(Arrays.asList(user1, user2, user3)));
        
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(sessionRepository.save(any(Session.class))).thenReturn(session);
        
        // ACT - Remove user2
        sessionService.noLongerParticipate(sessionId, userId2);
        
        // ASSERT - user1 and user3 remain, user2 was removed
        assertEquals(2, session.getUsers().size());
        assertTrue(session.getUsers().stream().anyMatch(u -> u.getId().equals(userId1)));
        assertTrue(session.getUsers().stream().anyMatch(u -> u.getId().equals(userId3)));
        assertFalse(session.getUsers().stream().anyMatch(u -> u.getId().equals(userId2)));
    }
}
