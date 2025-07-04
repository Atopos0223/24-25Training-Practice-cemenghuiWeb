package com.example.cemenghui_web;

import com.example.cemenghui_web.controller.CourseController;
import com.example.cemenghui_web.entity.Course;
import com.example.cemenghui_web.mapper.CourseMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CourseControllerTest {

    @Mock
    private CourseMapper courseMapper;

    @InjectMocks
    private CourseController courseController;

    private MultipartFile mockCoverFile;
    private MultipartFile mockVideoFile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockCoverFile = new MockMultipartFile(
                "cover",
                "test.jpg",
                "image/jpeg",
                "test image".getBytes()
        );
        mockVideoFile = new MockMultipartFile(
                "video",
                "test.mp4",
                "video/mp4",
                "test video".getBytes()
        );
    }

    // 测试添加课程成功
    @Test
    void testAddCourseSuccess() throws Exception {
        when(courseMapper.insertCourse(any(Course.class))).thenReturn(1);

        Map<String, Object> result = courseController.addCourse(
                "测试课程",
                "测试作者",
                mockCoverFile,
                "课程介绍",
                mockVideoFile,
                "已发布"
        );

        assertTrue((Boolean) result.get("success"));
        assertNotNull(result.get("data"));
        verify(courseMapper, times(1)).insertCourse(any(Course.class));
    }


    @Test
    void testAddCourseWithEmptyCoverFile() {
        // 创建一个空文件（不是null）
        MultipartFile emptyFile = new MockMultipartFile(
                "empty",
                "empty.jpg",
                "image/jpeg",
                new byte[0]
        );

        Map<String, Object> result = courseController.addCourse(
                "测试课程",
                "测试作者",
                emptyFile,
                "课程介绍",
                null,
                "已发布"
        );

        assertFalse((Boolean) result.get("success"));
        assertEquals("课程封面不能为空", result.get("message"));
    }

    // 测试数据库插入失败
    @Test
    void testAddCourseWithDatabaseFailure() {
        when(courseMapper.insertCourse(any(Course.class))).thenReturn(0);

        Map<String, Object> result = courseController.addCourse(
                "测试课程",
                "测试作者",
                mockCoverFile,
                "课程介绍",
                null,
                "已发布"
        );

        assertFalse((Boolean) result.get("success"));
        assertEquals("数据库插入失败", result.get("message"));
    }

    // 测试获取课程列表成功
    @Test
    void testGetCourseListSuccess() {
        List<Course> mockCourses = Arrays.asList(new Course(), new Course());
        when(courseMapper.selectAllCourses()).thenReturn(mockCourses);

        Map<String, Object> result = courseController.getCourseList();

        assertTrue((Boolean) result.get("success"));
        assertEquals(2, ((List<?>) result.get("data")).size());
    }

    // 测试获取课程列表异常
    @Test
    void testGetCourseListWithException() {
        when(courseMapper.selectAllCourses()).thenThrow(new RuntimeException("数据库错误"));

        Map<String, Object> result = courseController.getCourseList();

        assertFalse((Boolean) result.get("success"));
        assertTrue(((String) result.get("message")).contains("查询失败"));
    }

    // 测试获取课程详情成功
    @Test
    void testGetCourseDetailSuccess() {
        Course mockCourse = new Course();
        mockCourse.setId(1);
        when(courseMapper.selectById(1L)).thenReturn(mockCourse);

        Map<String, Object> result = courseController.getCourseDetail(1L);

        assertTrue((Boolean) result.get("success"));
        assertNotNull(result.get("data"));
    }

    // 测试获取不存在的课程详情
    @Test
    void testGetNonExistentCourseDetail() {
        when(courseMapper.selectById(999L)).thenReturn(null);

        Map<String, Object> result = courseController.getCourseDetail(999L);

        assertFalse((Boolean) result.get("success"));
        assertEquals("课程不存在", result.get("message"));
    }

    // 测试更新课程成功
    @Test
    void testUpdateCourseSuccess() throws Exception {
        Course existingCourse = new Course();
        existingCourse.setId(1);
        when(courseMapper.selectById(1L)).thenReturn(existingCourse);
        when(courseMapper.updateCourse(any(Course.class))).thenReturn(1);

        Map<String, Object> result = courseController.editCourse(
                1L,
                "新标题",
                "新作者",
                mockCoverFile,
                "新介绍",
                null,
                "已发布"
        );

        assertTrue((Boolean) result.get("success"));
        verify(courseMapper, times(1)).updateCourse(any(Course.class));
    }

    // 测试删除课程成功
    @Test
    void testDeleteCourseSuccess() {
        when(courseMapper.deleteById(1L)).thenReturn(1);

        Map<String, Object> result = courseController.deleteCourse(1L);

        assertTrue((Boolean) result.get("success"));
        assertEquals("删除成功", result.get("message"));
    }

    // 测试审核课程通过
    @Test
    void testAuditCoursePass() {
        Course mockCourse = new Course();
        when(courseMapper.selectById(1L)).thenReturn(mockCourse);
        when(courseMapper.updateCourse(any(Course.class))).thenReturn(1);

        Map<String, Object> result = courseController.auditCourse(
                1L,
                "pass",
                null
        );

        assertTrue((Boolean) result.get("success"));
        assertEquals("已发布", mockCourse.getStatus());
    }

    // 测试审核课程拒绝
    @Test
    void testAuditCourseReject() {
        Course mockCourse = new Course();
        when(courseMapper.selectById(1L)).thenReturn(mockCourse);
        when(courseMapper.updateCourse(any(Course.class))).thenReturn(1);

        Map<String, Object> result = courseController.auditCourse(
                1L,
                "reject",
                "内容不符合要求"
        );

        assertTrue((Boolean) result.get("success"));
        assertEquals("未通过", mockCourse.getStatus());
        assertEquals("内容不符合要求", mockCourse.getAuditComment());
    }

    // 测试无效的审核操作
    @Test
    void testAuditCourseWithInvalidAction() {
        Course mockCourse = new Course();
        when(courseMapper.selectById(1L)).thenReturn(mockCourse);

        Map<String, Object> result = courseController.auditCourse(
                1L,
                "invalid_action",
                null
        );

        assertFalse((Boolean) result.get("success"));
        assertEquals("无效操作类型", result.get("message"));
    }
}