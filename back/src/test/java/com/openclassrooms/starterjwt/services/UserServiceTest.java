apackage com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService
 * Uses Mockito to mock the UserRepository
 * 
 * Structure of each test:
 * 1. ARRANGE: prepare data and configure mocks
 * 2. ACT: call the method to test
 * 3. ASSERT: verify the result
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    /**
     * @Mock: creates a "fake" repository that doesn't actually make database calls
     */
    @Mock
    private UserRepository userRepository;

    /**
     * @InjectMocks: creates a real instance of UserService
     * and automatically injects the mock repository into it
     */
    @InjectMocks
    private UserService userService;

    private User testUser;

    /**
     * Method executed BEFORE each test
     * Prepares common data
     */
    @BeforeEach
    void setUp() {
        // Create a reusable test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("yoga@studio.com");
        testUser.setFirstName("Yoga");
        testUser.setLastName("Studio");
        testUser.setPassword("test!1234");
        testUser.setAdmin(false);
    }

    /**
     * Test 1: findById with an existing user
     * Scenario: search for a user that exists in database
     * Expected result: returns the found user
     */
    @Test
    void testFindById_UserExists_ReturnsUser() {
        // ARRANGE: configure mock to return the user
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // ACT: call the service method
        User result = userService.findById(1L);

        // ASSERT: verify the result
        assertNotNull(result, "User should not be null");
        assertEquals(1L, result.getId(), "ID should be 1");
        assertEquals("yoga@studio.com", result.getEmail(), "Email should match");
        assertEquals("Yoga", result.getFirstName(), "First name should match");

        // Verify that repository was called once
        verify(userRepository, times(1)).findById(1L);
    }

    /**
     * Test 2: findById with a non-existing user
     * Scenario: search for a user that doesn't exist
     * Expected result: returns null
     */
    @Test
    void testFindById_UserNotExists_ReturnsNull() {
        // ARRANGE: configure mock to return Optional.empty()
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // ACT: call the method
        User result = userService.findById(999L);

        // ASSERT: verify that null is returned
        assertNull(result, "Result should be null for non-existing user");

        // Verify that repository was called
        verify(userRepository, times(1)).findById(999L);
    }

    /**
     * Test 3: delete calls the repository correctly
     * Scenario: delete a user
     * Expected result: repository is called with the correct ID
     */
    @Test
    void testDelete_CallsRepository() {
        // ARRANGE: no special configuration needed
        // doNothing() is the default behavior for void methods

        // ACT: call delete
        userService.delete(1L);

        // ASSERT: verify that deleteById was called with the correct ID
        verify(userRepository, times(1)).deleteById(1L);
    }

    /**
     * Test 4: delete with null ID
     * Scenario: attempt to delete with null ID
     * Expected result: repository is still called (current behavior)
     * Note: in a real project, we could throw an exception
     */
    @Test
    void testDelete_WithNullId_CallsRepository() {
        // ACT: call delete with null
        userService.delete(null);

        // ASSERT: verify that repository was called with null
        verify(userRepository, times(1)).deleteById(null);
    }

    /**
     * Test 5: findById with different IDs
     * Scenario: test with multiple different IDs
     * Expected result: repository is called with each ID
     */
    @Test
    void testFindById_MultipleIds_CallsRepositoryEachTime() {
        // ARRANGE: configure multiple returns
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        when(userRepository.findById(3L)).thenReturn(Optional.empty());

        // ACT: call multiple times
        userService.findById(1L);
        userService.findById(2L);
        userService.findById(3L);

        // ASSERT: verify each call
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(2L);
        verify(userRepository, times(1)).findById(3L);
    }
}
