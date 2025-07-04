package com.example.cemenghui_web.service;

import com.example.cemenghui_web.entity.Course;
import com.example.cemenghui_web.mapper.CourseMapper;
import com.example.cemenghui_web.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseServiceTest {

    @Mock
    private CourseMapper courseMapper;

    @InjectMocks
    private CourseService courseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddCourseSuccess() {
        Course course = new Course();
        when(courseMapper.insertCourse(course)).thenReturn(1);
        boolean result = courseService.addCourse(course);
        assertTrue(result);
        verify(courseMapper, times(1)).insertCourse(course);
    }

    @Test
    void testAddCourseFail() {
        Course course = new Course();
        when(courseMapper.insertCourse(course)).thenReturn(0);
        boolean result = courseService.addCourse(course);
        assertFalse(result);
        verify(courseMapper, times(1)).insertCourse(course);
    }

    @Test
    void testListAllCourses() {
        Course course1 = new Course();
        Course course2 = new Course();
        List<Course> expectedCourses = Arrays.asList(course1, course2);
        when(courseMapper.listAll()).thenReturn(expectedCourses);
        List<Course> actualCourses = courseService.listAll();
        assertEquals(2, actualCourses.size());
        verify(courseMapper, times(1)).listAll();
    }
}
