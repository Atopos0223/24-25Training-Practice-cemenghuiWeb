package com.example.cemenghui_web.controller;

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

import java.io.IOException;
import java.nio.file.Path;
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

    // addCourse cover为null
    @Test
    void testAddCourseCoverNull() {
        Map<String, Object> result = courseController.addCourse(
                "标题", "作者", null, "介绍", null, "已发布");
        assertFalse((Boolean) result.get("success"));
    }

    // addCourse cover为空
    @Test
    void testAddCourseCoverEmpty() {
        MultipartFile emptyFile = new MockMultipartFile(
                "cover", "empty.jpg", "image/jpeg", new byte[0]
        );
        Map<String, Object> result = courseController.addCourse(
                "标题", "作者", emptyFile, "介绍", null, "已发布");
        assertFalse((Boolean) result.get("success"));
        assertEquals("课程封面不能为空", result.get("message"));
    }

    // addCourse video为空
    @Test
    void testAddCourseVideoNull() {
        when(courseMapper.insertCourse(any(Course.class))).thenReturn(1);
        MultipartFile cover = new MockMultipartFile(
                "cover", "cover.jpg", "image/jpeg", "cover".getBytes()
        );
        Map<String, Object> result = courseController.addCourse(
                "标题", "作者", cover, "介绍", null, "已发布");
        assertTrue((Boolean) result.get("success"));
        assertNotNull(result.get("data"));
    }

    // addCourse video正常
    @Test
    void testAddCourseVideoNormal() {
        when(courseMapper.insertCourse(any(Course.class))).thenReturn(1);
        MultipartFile cover = new MockMultipartFile(
                "cover", "cover.jpg", "image/jpeg", "cover".getBytes()
        );
        MultipartFile video = new MockMultipartFile(
                "video", "video.mp4", "video/mp4", "video".getBytes()
        );
        Map<String, Object> result = courseController.addCourse(
                "标题", "作者", cover, "介绍", video, "已发布");
        assertTrue((Boolean) result.get("success"));
        assertNotNull(result.get("data"));
    }

    // addCourse video为null
    @Test
    void testAddCourseVideoNullExplicit() {
        when(courseMapper.insertCourse(any(Course.class))).thenReturn(1);
        Map<String, Object> result = courseController.addCourse(
                "标题", "作者", mockCoverFile, "介绍", null, "已发布");
        assertTrue((Boolean) result.get("success"));
        assertNotNull(result.get("data"));
    }

    // addCourse video为空文件
    @Test
    void testAddCourseVideoEmptyFile() {
        when(courseMapper.insertCourse(any(Course.class))).thenReturn(1);
        MultipartFile emptyVideo = new MockMultipartFile("video", "empty.mp4", "video/mp4", new byte[0]);
        Map<String, Object> result = courseController.addCourse(
                "标题", "作者", mockCoverFile, "介绍", emptyVideo, "已发布");
        assertTrue((Boolean) result.get("success"));
        assertNotNull(result.get("data"));
    }

    // addCourse 文件保存异常
    @Test
    void testAddCourseFileSaveException() throws Exception {
        MultipartFile cover = mock(MultipartFile.class);
        when(cover.isEmpty()).thenReturn(false);
        when(cover.getOriginalFilename()).thenReturn("cover.jpg");
        doThrow(new IOException("保存失败")).when(cover).transferTo(any(Path.class));
        Map<String, Object> result = courseController.addCourse(
                "标题", "作者", cover, "介绍", null, "已发布");
        assertFalse((Boolean) result.get("success"));
        assertTrue(((String) result.get("message")).contains("保存失败"));
    }

    // addCourse 数据库插入失败
    @Test
    void testAddCourseDatabaseInsertFail() {
        when(courseMapper.insertCourse(any(Course.class))).thenReturn(0);
        MultipartFile cover = new MockMultipartFile(
                "cover", "cover.jpg", "image/jpeg", "cover".getBytes()
        );
        Map<String, Object> result = courseController.addCourse(
                "标题", "作者", cover, "介绍", null, "已发布");
        assertFalse((Boolean) result.get("success"));
        assertEquals("数据库插入失败", result.get("message"));
    }

    // addCourse 参数异常
    @Test
    void testAddCourseParamException() {
        MultipartFile cover = new MockMultipartFile(
                "cover", "cover.jpg", "image/jpeg", "cover".getBytes()
        );
        Map<String, Object> result = courseController.addCourse(
                null, null, cover, null, null, null);
        assertFalse((Boolean) result.get("success"));
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

    // getCourseList 返回空列表
    @Test
    void testGetCourseListEmpty() {
        when(courseMapper.selectAllCourses()).thenReturn(Collections.emptyList());
        Map<String, Object> result = courseController.getCourseList();
        assertTrue((Boolean) result.get("success"));
        assertNotNull(result.get("data"));
        assertTrue(((List<?>) result.get("data")).isEmpty());
    }

    // getCourseList 异常
    @Test
    void testGetCourseListException() {
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

    // getCourseDetail 课程不存在
    @Test
    void testGetCourseDetailNotExist() {
        when(courseMapper.selectById(anyLong())).thenReturn(null);
        Map<String, Object> result = courseController.getCourseDetail(999L);
        assertFalse((Boolean) result.get("success"));
        assertEquals("课程不存在", result.get("message"));
    }

    // getCourseDetail 异常
    @Test
    void testGetCourseDetailException() {
        when(courseMapper.selectById(anyLong())).thenThrow(new RuntimeException("数据库错误"));
        Map<String, Object> result = courseController.getCourseDetail(1L);
        assertFalse((Boolean) result.get("success"));
        assertEquals("服务器错误", result.get("message"));
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

    // getAuditingCourses 返回空列表
    @Test
    void testGetAuditingCoursesEmpty() {
        when(courseMapper.selectByStatus(anyString())).thenReturn(Collections.emptyList());
        Map<String, Object> result = courseController.getAuditingCourses();
        assertTrue((Boolean) result.get("success"));
        assertNotNull(result.get("data"));
        assertTrue(((List<?>) result.get("data")).isEmpty());
    }

    // getAuditingCourses 异常
    @Test
    void testGetAuditingCoursesException() {
        when(courseMapper.selectByStatus(anyString())).thenThrow(new RuntimeException("数据库错误"));
        Map<String, Object> result = courseController.getAuditingCourses();
        assertFalse((Boolean) result.get("success"));
        assertTrue(((String) result.get("message")).contains("查询失败"));
    }

    // getManagedCourses 返回空列表
    @Test
    void testGetManagedCoursesEmpty() {
        when(courseMapper.selectByStatusNot(anyString())).thenReturn(Collections.emptyList());
        Map<String, Object> result = courseController.getManagedCourses();
        assertTrue((Boolean) result.get("success"));
        assertNotNull(result.get("data"));
        assertTrue(((List<?>) result.get("data")).isEmpty());
    }

    // getManagedCourses 异常
    @Test
    void testGetManagedCoursesException() {
        when(courseMapper.selectByStatusNot(anyString())).thenThrow(new RuntimeException("数据库错误"));
        Map<String, Object> result = courseController.getManagedCourses();
        assertFalse((Boolean) result.get("success"));
        assertTrue(((String) result.get("message")).contains("查询失败"));
    }

    // editCourse 课程不存在
    @Test
    void testEditCourseNotExist() {
        when(courseMapper.selectById(anyLong())).thenReturn(null);
        Map<String, Object> result = courseController.editCourse(1L, "新标题", "新作者", null, null, null, null);
        assertFalse((Boolean) result.get("success"));
        assertEquals("课程不存在", result.get("message"));
    }

    // editCourse cover/video为空
    @Test
    void testEditCourseCoverVideoEmpty() {
        Course existing = new Course();
        when(courseMapper.selectById(anyLong())).thenReturn(existing);
        when(courseMapper.updateCourse(any(Course.class))).thenReturn(1);
        MultipartFile emptyCover = new MockMultipartFile("cover", "empty.jpg", "image/jpeg", new byte[0]);
        MultipartFile emptyVideo = new MockMultipartFile("video", "empty.mp4", "video/mp4", new byte[0]);
        Map<String, Object> result = courseController.editCourse(1L, null, null, emptyCover, null, emptyVideo, null);
        assertTrue((Boolean) result.get("success"));
        assertNotNull(result.get("data"));
    }

    // editCourse cover/video正常
    @Test
    void testEditCourseCoverVideoNormal() {
        Course existing = new Course();
        when(courseMapper.selectById(anyLong())).thenReturn(existing);
        when(courseMapper.updateCourse(any(Course.class))).thenReturn(1);
        MultipartFile cover = new MockMultipartFile("cover", "cover.jpg", "image/jpeg", "cover".getBytes());
        MultipartFile video = new MockMultipartFile("video", "video.mp4", "video/mp4", "video".getBytes());
        Map<String, Object> result = courseController.editCourse(1L, "新标题", "新作者", cover, "新介绍", video, "已发布");
        assertTrue((Boolean) result.get("success"));
        assertNotNull(result.get("data"));
    }

    // editCourse 数据库更新失败
    @Test
    void testEditCourseDatabaseUpdateFail() {
        Course existing = new Course();
        when(courseMapper.selectById(anyLong())).thenReturn(existing);
        when(courseMapper.updateCourse(any(Course.class))).thenReturn(0);
        MultipartFile cover = new MockMultipartFile("cover", "cover.jpg", "image/jpeg", "cover".getBytes());
        Map<String, Object> result = courseController.editCourse(1L, "新标题", "新作者", cover, "新介绍", null, "已发布");
        assertFalse((Boolean) result.get("success"));
        assertEquals("数据库更新失败", result.get("message"));
    }

    // editCourse 异常
    @Test
    void testEditCourseException() {
        when(courseMapper.selectById(anyLong())).thenThrow(new RuntimeException("数据库错误"));
        Map<String, Object> result = courseController.editCourse(1L, null, null, null, null, null, null);
        assertFalse((Boolean) result.get("success"));
        assertNotNull(result.get("message"));
    }

    // deleteCourse 课程不存在
    @Test
    void testDeleteCourseNotExist() {
        when(courseMapper.deleteById(anyLong())).thenReturn(0);
        Map<String, Object> result = courseController.deleteCourse(1L);
        assertFalse((Boolean) result.get("success"));
        assertTrue(((String) result.get("message")).contains("删除失败"));
    }

    // deleteCourse 异常
    @Test
    void testDeleteCourseException() {
        when(courseMapper.deleteById(anyLong())).thenThrow(new RuntimeException("数据库错误"));
        Map<String, Object> result = courseController.deleteCourse(1L);
        assertFalse((Boolean) result.get("success"));
        assertTrue(((String) result.get("message")).contains("删除失败"));
    }

    // auditCourse 参数缺失
    @Test
    void testAuditCourseParamMissing() {
        Map<String, Object> result = courseController.auditCourse(null, null, null);
        assertFalse((Boolean) result.get("success"));
        assertEquals("无效操作类型", result.get("message"));
    }

    // auditCourse action非法
    @Test
    void testAuditCourseInvalidAction() {
        Course course = new Course();
        when(courseMapper.selectById(anyLong())).thenReturn(course);
        Map<String, Object> result = courseController.auditCourse(1L, "invalid_action", null);
        assertFalse((Boolean) result.get("success"));
        assertEquals("无效操作类型", result.get("message"));
    }

    // auditCourse 数据库更新失败
    @Test
    void testAuditCourseDatabaseUpdateFail() {
        Course course = new Course();
        when(courseMapper.selectById(anyLong())).thenReturn(course);
        when(courseMapper.updateCourse(any(Course.class))).thenReturn(0);
        Map<String, Object> result = courseController.auditCourse(1L, "pass", null);
        assertFalse((Boolean) result.get("success"));
        assertEquals("更新失败", result.get("message"));
    }

    // auditCourse 异常
    @Test
    void testAuditCourseException() {
        when(courseMapper.selectById(anyLong())).thenThrow(new RuntimeException("数据库错误"));
        Map<String, Object> result = courseController.auditCourse(1L, "pass", null);
        assertFalse((Boolean) result.get("success"));
        assertNotNull(result.get("message"));
    }

    // editCourse cover为null, video为null, 其它参数为空字符串
    @Test
    void testEditCourseAllNullOrEmpty() {
        Course existing = new Course();
        when(courseMapper.selectById(anyLong())).thenReturn(existing);
        when(courseMapper.updateCourse(any(Course.class))).thenReturn(1);
        Map<String, Object> result = courseController.editCourse(1L, "", "", null, "", null, "");
        assertTrue((Boolean) result.get("success"));
        assertNotNull(result.get("data"));
    }

    // fixUrl方法分支间接覆盖：url为null
    @Test
    void testFixUrlNull() {
        Course course = new Course();
        course.setCoverUrl(null);
        course.setVideoUrl(null);
        when(courseMapper.selectById(1L)).thenReturn(course);
        Map<String, Object> result = courseController.getCourseDetail(1L);
        assertTrue((Boolean) result.get("success"));
        assertNull(((Course) result.get("data")).getCoverUrl());
        assertNull(((Course) result.get("data")).getVideoUrl());
    }

    // fixUrl方法分支间接覆盖：url为http开头
    @Test
    void testFixUrlHttp() {
        Course course = new Course();
        course.setCoverUrl("http://example.com/cover.jpg");
        course.setVideoUrl("https://example.com/video.mp4");
        when(courseMapper.selectById(2L)).thenReturn(course);
        Map<String, Object> result = courseController.getCourseDetail(2L);
        assertTrue((Boolean) result.get("success"));
        assertEquals("http://example.com/cover.jpg", ((Course) result.get("data")).getCoverUrl());
        assertEquals("https://example.com/video.mp4", ((Course) result.get("data")).getVideoUrl());
    }

    // fixUrl方法分支间接覆盖：url为/uploads/xxx
    @Test
    void testFixUrlWithUploadsPrefix() {
        Course course = new Course();
        course.setCoverUrl("/uploads/cover.jpg");
        course.setVideoUrl("uploads/video.mp4");
        when(courseMapper.selectById(3L)).thenReturn(course);
        Map<String, Object> result = courseController.getCourseDetail(3L);
        assertTrue((Boolean) result.get("success"));
        assertEquals("/uploads/cover.jpg", ((Course) result.get("data")).getCoverUrl());
        assertEquals("/uploads/video.mp4", ((Course) result.get("data")).getVideoUrl());
    }

    // auditCourse id为null
    @Test
    void testAuditCourseIdNull() {
        Map<String, Object> result = courseController.auditCourse(null, "pass", null);
        assertFalse((Boolean) result.get("success"));
        assertEquals("课程不存在", result.get("message"));
    }

    // auditCourse action为null
    @Test
    void testAuditCourseActionNull() {
        Map<String, Object> result = courseController.auditCourse(1L, null, null);
        assertFalse((Boolean) result.get("success"));
        assertEquals("无效操作类型", result.get("message"));
    }

    // auditCourse action为reject但comment为空
    @Test
    void testAuditCourseRejectCommentEmpty() {
        Map<String, Object> result = courseController.auditCourse(1L, "reject", "");
        assertFalse((Boolean) result.get("success"));
        assertEquals("拒绝原因不能为空", result.get("message"));
    }

    // auditCourse selectById返回null
    @Test
    void testAuditCourseCourseNotExist() {
        when(courseMapper.selectById(anyLong())).thenReturn(null);
        Map<String, Object> result = courseController.auditCourse(1L, "pass", null);
        assertFalse((Boolean) result.get("success"));
        assertEquals("课程不存在", result.get("message"));
    }

    // auditCourse selectById抛异常
    @Test
    void testAuditCourseSelectByIdException() {
        when(courseMapper.selectById(anyLong())).thenThrow(new RuntimeException("数据库错误"));
        Map<String, Object> result = courseController.auditCourse(1L, "pass", null);
        assertFalse((Boolean) result.get("success"));
        assertTrue(((String) result.get("message")).contains("数据库错误"));
    }

    // addCourse 上传目录创建失败/文件保存异常（通过 transferTo 抛异常间接覆盖）
    @Test
    void testAddCourseCreateDirOrSaveFail() throws Exception {
        MultipartFile cover = mock(MultipartFile.class);
        when(cover.isEmpty()).thenReturn(false);
        when(cover.getOriginalFilename()).thenReturn("cover.jpg");
        doThrow(new IOException("目录创建失败")).when(cover).transferTo(any(Path.class));
        Map<String, Object> result = courseController.addCourse("标题", "作者", cover, "介绍", null, "已发布");
        assertFalse((Boolean) result.get("success"));
        assertTrue(((String) result.get("message")).contains("目录创建失败") || ((String) result.get("message")).contains("保存失败"));
    }

    // addCourse saveFile(video) 抛异常（通过 video.transferTo 抛异常间接覆盖）
    @Test
    void testAddCourseSaveVideoException() throws Exception {
        MultipartFile cover = new MockMultipartFile("cover", "cover.jpg", "image/jpeg", "cover".getBytes());
        MultipartFile video = mock(MultipartFile.class);
        when(video.isEmpty()).thenReturn(false);
        when(video.getOriginalFilename()).thenReturn("video.mp4");
        doThrow(new IOException("视频保存失败")).when(video).transferTo(any(Path.class));
        when(courseMapper.insertCourse(any(Course.class))).thenReturn(1);
        Map<String, Object> result = courseController.addCourse("标题", "作者", cover, "介绍", video, "已发布");
        assertFalse((Boolean) result.get("success"));
        assertTrue(((String) result.get("message")).contains("视频保存失败") || ((String) result.get("message")).contains("保存失败"));
    }

    // auditCourse action为reject且comment为null
    @Test
    void testAuditCourseRejectCommentNull() {
        Map<String, Object> result = courseController.auditCourse(1L, "reject", null);
        assertFalse((Boolean) result.get("success"));
        assertEquals("拒绝原因不能为空", result.get("message"));
    }

    // addCourse cover.getOriginalFilename() 返回 null
    @Test
    void testAddCourseCoverOriginalFilenameNull() throws Exception {
        MultipartFile cover = mock(MultipartFile.class);
        when(cover.isEmpty()).thenReturn(false);
        when(cover.getOriginalFilename()).thenReturn(null);
        doNothing().when(cover).transferTo(any(Path.class));
        when(courseMapper.insertCourse(any(Course.class))).thenReturn(1);
        Map<String, Object> result = courseController.addCourse("标题", "作者", cover, "介绍", null, "已发布");
        assertTrue((Boolean) result.get("success"));
    }

    // addCourse video.getOriginalFilename() 返回 null
    @Test
    void testAddCourseVideoOriginalFilenameNull() throws Exception {
        MultipartFile cover = new MockMultipartFile("cover", "cover.jpg", "image/jpeg", "cover".getBytes());
        MultipartFile video = mock(MultipartFile.class);
        when(video.isEmpty()).thenReturn(false);
        when(video.getOriginalFilename()).thenReturn(null);
        doNothing().when(video).transferTo(any(Path.class));
        when(courseMapper.insertCourse(any(Course.class))).thenReturn(1);
        Map<String, Object> result = courseController.addCourse("标题", "作者", cover, "介绍", video, "已发布");
        assertTrue((Boolean) result.get("success"));
    }
}