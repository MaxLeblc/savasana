package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void findById_shouldReturnUser_whenUserExists() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository).findById(1L);
    }

    @Test
    void findById_shouldReturnNull_whenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        User result = userService.findById(999L);

        assertNull(result);
        verify(userRepository).findById(999L);
    }

    @Test
    void delete_shouldCallRepository() {
        userService.delete(1L);
        
        verify(userRepository).deleteById(1L);
    }
}
