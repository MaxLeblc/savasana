package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TeacherService
 * 
 * Simple service with only 2 methods:
 * - findAll(): retrieves all teachers
 * - findById(Long id): retrieves a teacher by ID
 * 
 * This is a good example of a "read-only" service without complex business logic.
 */
@ExtendWith(MockitoExtension.class)
public class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherService teacherService;

    // ========== Tests for FIND_ALL ==========

    @Test
    public void testFindAll_ReturnsAllTeachers() {
        // ARRANGE
        Teacher teacher1 = new Teacher();
        teacher1.setId(1L);
        teacher1.setFirstName("John");
        teacher1.setLastName("Doe");

        Teacher teacher2 = new Teacher();
        teacher2.setId(2L);
        teacher2.setFirstName("Jane");
        teacher2.setLastName("Smith");

        List<Teacher> teachers = Arrays.asList(teacher1, teacher2);

        when(teacherRepository.findAll()).thenReturn(teachers);

        // ACT
        List<Teacher> result = teacherService.findAll();

        // ASSERT
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John", result.get(0).getFirstName());
        assertEquals("Doe", result.get(0).getLastName());
        assertEquals("Jane", result.get(1).getFirstName());
        assertEquals("Smith", result.get(1).getLastName());
        
        verify(teacherRepository, times(1)).findAll();
    }

    @Test
    public void testFindAll_ReturnsEmptyList_WhenNoTeachers() {
        // ARRANGE - Empty database
        when(teacherRepository.findAll()).thenReturn(new ArrayList<>());

        // ACT
        List<Teacher> result = teacherService.findAll();

        // ASSERT
        assertNotNull(result);
        assertEquals(0, result.size());
        assertTrue(result.isEmpty());
        
        verify(teacherRepository, times(1)).findAll();
    }

    // ========== Tests for FIND_BY_ID ==========

    @Test
    public void testFindById_TeacherExists_ReturnsTeacher() {
        // ARRANGE
        Long teacherId = 1L;
        Teacher teacher = new Teacher();
        teacher.setId(teacherId);
        teacher.setFirstName("John");
        teacher.setLastName("Doe");

        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));

        // ACT
        Teacher result = teacherService.findById(teacherId);

        // ASSERT
        assertNotNull(result);
        assertEquals(teacherId, result.getId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        
        verify(teacherRepository, times(1)).findById(teacherId);
    }

    @Test
    public void testFindById_TeacherNotExists_ReturnsNull() {
        // ARRANGE
        Long teacherId = 999L;
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.empty());

        // ACT
        Teacher result = teacherService.findById(teacherId);

        // ASSERT
        assertNull(result);
        
        verify(teacherRepository, times(1)).findById(teacherId);
    }

    /**
     * Edge case test: verify behavior with null ID
     * 
     * Note: In a real case, the repository could throw an exception.
     * This test verifies that the service delegates correctly without adding logic.
     */
    @Test
    public void testFindById_WithNullId_CallsRepository() {
        // ARRANGE
        when(teacherRepository.findById(null)).thenReturn(Optional.empty());

        // ACT
        Teacher result = teacherService.findById(null);

        // ASSERT
        assertNull(result);
        verify(teacherRepository, times(1)).findById(null);
    }

    /**
     * Multiple calls verification test
     * 
     * Verifies that each call to findById() triggers a repository call.
     */
    @Test
    public void testFindById_MultipleIds_CallsRepositoryEachTime() {
        // ARRANGE
        Teacher teacher1 = new Teacher();
        teacher1.setId(1L);

        Teacher teacher2 = new Teacher();
        teacher2.setId(2L);

        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher1));
        when(teacherRepository.findById(2L)).thenReturn(Optional.of(teacher2));
        when(teacherRepository.findById(3L)).thenReturn(Optional.empty());

        // ACT - Make multiple calls
        Teacher result1 = teacherService.findById(1L);
        Teacher result2 = teacherService.findById(2L);
        Teacher result3 = teacherService.findById(3L);

        // ASSERT
        assertNotNull(result1);
        assertEquals(1L, result1.getId());

        assertNotNull(result2);
        assertEquals(2L, result2.getId());

        assertNull(result3);

        // Verify that findById() was called exactly once for each ID
        verify(teacherRepository, times(1)).findById(1L);
        verify(teacherRepository, times(1)).findById(2L);
        verify(teacherRepository, times(1)).findById(3L);
    }

    /**
     * Consistency test: verify that no modifications are made
     * 
     * TeacherService is a "read-only" service, it should never modify data.
     * This test verifies that no modification methods are called.
     */
    @Test
    public void testReadOnlyService_NeverCallsSaveOrDelete() {
        // ARRANGE
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(teacherRepository.findAll()).thenReturn(Arrays.asList(teacher));

        // ACT - Call all service methods
        teacherService.findById(1L);
        teacherService.findAll();

        // ASSERT - Verify that no modification methods were called
        verify(teacherRepository, never()).save(any());
        verify(teacherRepository, never()).delete(any());
        verify(teacherRepository, never()).deleteById(anyLong());
    }
}
