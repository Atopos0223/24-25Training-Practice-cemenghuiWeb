package com.example.cemenghui_web.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CourseTest {
    @Test
    void testCourseConstructorAndGetterSetter() {
        Course course = new Course();
        course.setId(1);
        course.setTitle("测试课程");
        course.setAuthor("作者");
        course.setIntro("简介");
        course.setCoverUrl("cover.jpg");
        course.setVideoUrl("video.mp4");
        course.setStatus("已发布");
        course.setAuditComment("通过");
        assertEquals(1, course.getId());
        assertEquals("测试课程", course.getTitle());
        assertEquals("作者", course.getAuthor());
        assertEquals("简介", course.getIntro());
        assertEquals("cover.jpg", course.getCoverUrl());
        assertEquals("video.mp4", course.getVideoUrl());
        assertEquals("已发布", course.getStatus());
        assertEquals("通过", course.getAuditComment());
    }
} 