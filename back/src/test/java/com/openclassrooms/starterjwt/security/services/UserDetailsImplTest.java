package com.openclassrooms.starterjwt.security.services;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserDetailsImplTest {

    @Test
    void equals_shouldReturnTrue_whenComparingWithItself() {
        UserDetailsImpl user = UserDetailsImpl.builder()
                .id(1L)
                .username("test@test.com")
                .firstName("Test")
                .lastName("User")
                .password("password")
                .admin(false)
                .build();

        assertTrue(user.equals(user));
    }

    @Test
    void equals_shouldReturnFalse_whenComparingWithNull() {
        UserDetailsImpl user = UserDetailsImpl.builder()
                .id(1L)
                .username("test@test.com")
                .firstName("Test")
                .lastName("User")
                .password("password")
                .admin(false)
                .build();

        assertFalse(user.equals(null));
    }

    @Test
    void equals_shouldReturnFalse_whenComparingWithDifferentClass() {
        UserDetailsImpl user = UserDetailsImpl.builder()
                .id(1L)
                .username("test@test.com")
                .firstName("Test")
                .lastName("User")
                .password("password")
                .admin(false)
                .build();

        assertFalse(user.equals("Not a UserDetailsImpl"));
    }

    @Test
    void equals_shouldReturnTrue_whenComparingWithSameId() {
        UserDetailsImpl user1 = UserDetailsImpl.builder()
                .id(1L)
                .username("test1@test.com")
                .firstName("Test1")
                .lastName("User1")
                .password("password1")
                .admin(false)
                .build();

        UserDetailsImpl user2 = UserDetailsImpl.builder()
                .id(1L)
                .username("test2@test.com")
                .firstName("Test2")
                .lastName("User2")
                .password("password2")
                .admin(true)
                .build();

        assertTrue(user1.equals(user2));
    }

    @Test
    void equals_shouldReturnFalse_whenComparingWithDifferentId() {
        UserDetailsImpl user1 = UserDetailsImpl.builder()
                .id(1L)
                .username("test@test.com")
                .firstName("Test")
                .lastName("User")
                .password("password")
                .admin(false)
                .build();

        UserDetailsImpl user2 = UserDetailsImpl.builder()
                .id(2L)
                .username("test@test.com")
                .firstName("Test")
                .lastName("User")
                .password("password")
                .admin(false)
                .build();

        assertFalse(user1.equals(user2));
    }

    @Test
    void getAuthorities_shouldReturnEmptyCollection() {
        UserDetailsImpl user = UserDetailsImpl.builder()
                .id(1L)
                .username("test@test.com")
                .firstName("Test")
                .lastName("User")
                .password("password")
                .admin(false)
                .build();

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        assertNotNull(authorities);
        assertTrue(authorities.isEmpty());
    }

    @Test
    void isAccountNonExpired_shouldReturnTrue() {
        UserDetailsImpl user = UserDetailsImpl.builder()
                .id(1L)
                .username("test@test.com")
                .firstName("Test")
                .lastName("User")
                .password("password")
                .admin(false)
                .build();

        assertTrue(user.isAccountNonExpired());
    }

    @Test
    void isAccountNonLocked_shouldReturnTrue() {
        UserDetailsImpl user = UserDetailsImpl.builder()
                .id(1L)
                .username("test@test.com")
                .firstName("Test")
                .lastName("User")
                .password("password")
                .admin(false)
                .build();

        assertTrue(user.isAccountNonLocked());
    }

    @Test
    void isCredentialsNonExpired_shouldReturnTrue() {
        UserDetailsImpl user = UserDetailsImpl.builder()
                .id(1L)
                .username("test@test.com")
                .firstName("Test")
                .lastName("User")
                .password("password")
                .admin(false)
                .build();

        assertTrue(user.isCredentialsNonExpired());
    }

    @Test
    void isEnabled_shouldReturnTrue() {
        UserDetailsImpl user = UserDetailsImpl.builder()
                .id(1L)
                .username("test@test.com")
                .firstName("Test")
                .lastName("User")
                .password("password")
                .admin(false)
                .build();

        assertTrue(user.isEnabled());
    }

    @Test
    void getters_shouldReturnCorrectValues() {
        UserDetailsImpl user = UserDetailsImpl.builder()
                .id(1L)
                .username("test@test.com")
                .firstName("Test")
                .lastName("User")
                .password("password")
                .admin(true)
                .build();

        assertEquals(1L, user.getId());
        assertEquals("test@test.com", user.getUsername());
        assertEquals("Test", user.getFirstName());
        assertEquals("User", user.getLastName());
        assertEquals("password", user.getPassword());
        assertTrue(user.getAdmin());
    }
}
