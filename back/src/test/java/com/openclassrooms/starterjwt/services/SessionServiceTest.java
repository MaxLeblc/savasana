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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SessionService sessionService;

    @Test
    public void create_shouldSaveSession() {
        Session session = new Session();
        session.setName("Yoga");
        
        when(sessionRepository.save(any(Session.class))).thenReturn(session);
        
        Session result = sessionService.create(session);
        
        assertNotNull(result);
        verify(sessionRepository).save(session);
    }

    @Test
    public void delete_shouldCallRepository() {
        sessionService.delete(1L);
        verify(sessionRepository).deleteById(1L);
    }

    @Test
    public void findAll_shouldReturnSessions() {
        List<Session> sessions = Arrays.asList(new Session(), new Session());
        when(sessionRepository.findAll()).thenReturn(sessions);
        
        List<Session> result = sessionService.findAll();
        
        assertEquals(2, result.size());
    }

    @Test
    public void getById_shouldReturnSession_whenExists() {
        Session session = new Session();
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        
        Session result = sessionService.getById(1L);
        
        assertNotNull(result);
    }

    @Test
    public void getById_shouldReturnNull_whenNotFound() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());
        
        Session result = sessionService.getById(1L);
        
        assertNull(result);
    }

    @Test
    public void update_shouldSaveSessionWithId() {
        Session session = new Session();
        when(sessionRepository.save(any(Session.class))).thenReturn(session);
        
        Session result = sessionService.update(1L, session);
        
        assertNotNull(result);
        verify(sessionRepository).save(any(Session.class));
    }

    @Test
    public void participate_shouldAddUserToSession() {
        Session session = new Session();
        session.setUsers(new ArrayList<>());
        User user = new User();
        user.setId(1L);
        
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        
        sessionService.participate(1L, 1L);
        
        assertEquals(1, session.getUsers().size());
        verify(sessionRepository).save(session);
    }

    @Test
    public void participate_shouldThrowNotFoundException_whenSessionNotFound() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        
        assertThrows(NotFoundException.class, () -> 
            sessionService.participate(1L, 1L));
    }

    @Test
    public void participate_shouldThrowBadRequestException_whenAlreadyParticipating() {
        User user = new User();
        user.setId(1L);
        Session session = new Session();
        session.setUsers(new ArrayList<>(Arrays.asList(user)));
        
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        
        assertThrows(BadRequestException.class, () -> 
            sessionService.participate(1L, 1L));
    }

    @Test
    public void noLongerParticipate_shouldRemoveUser() {
        User user = new User();
        user.setId(1L);
        Session session = new Session();
        session.setUsers(new ArrayList<>(Arrays.asList(user)));
        
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        
        sessionService.noLongerParticipate(1L, 1L);
        
        assertEquals(0, session.getUsers().size());
    }

    @Test
    public void noLongerParticipate_shouldThrowNotFoundException_whenSessionNotFound() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());
        
        assertThrows(NotFoundException.class, () -> 
            sessionService.noLongerParticipate(1L, 1L));
    }

    @Test
    public void noLongerParticipate_shouldThrowBadRequestException_whenNotParticipating() {
        Session session = new Session();
        session.setUsers(new ArrayList<>());
        
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        
        assertThrows(BadRequestException.class, () -> 
            sessionService.noLongerParticipate(1L, 1L));
    }
}
