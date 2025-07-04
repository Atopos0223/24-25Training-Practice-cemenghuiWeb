package com.example.cemenghui_web.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

public class MeetingTest {
    @Test
    void testAllGetterSetter() {
        Meeting meeting = new Meeting();
        Date now = new Date();
        meeting.setId(1L);
        meeting.setTitle("会议标题");
        meeting.setContent("内容");
        meeting.setStart_time(now);
        meeting.setEnd_time(now);
        meeting.setCreator_id(2L);
        meeting.setStatus(1);
        meeting.setLocation("会议室A");
        meeting.setCover("cover.jpg");
        meeting.setCreate_time(now);
        meeting.setCreator_name("张三");
        // 兼容重载方法
        meeting.setCreatorId(3L);
        meeting.setCreateTime(now);
        // 断言所有getter
        assertEquals(1L, meeting.getId());
        assertEquals("会议标题", meeting.getTitle());
        assertEquals("内容", meeting.getContent());
        assertEquals(now, meeting.getStart_time());
        assertEquals(now, meeting.getEnd_time());
        assertEquals(3L, meeting.getCreatorId());
        assertEquals(3L, meeting.getCreator_id());
        assertEquals(1, meeting.getStatus());
        assertEquals("会议室A", meeting.getLocation());
        assertEquals("cover.jpg", meeting.getCover());
        assertEquals(now, meeting.getCreate_time());
        assertEquals(now, meeting.getCreateTime());
        assertEquals("张三", meeting.getCreator_name());
    }

    @Test
    void testToString() {
        Meeting meeting = new Meeting();
        meeting.setId(1L);
        meeting.setTitle("会议标题");
        meeting.setContent("内容");
        meeting.setStart_time(new Date(0));
        meeting.setEnd_time(new Date(0));
        meeting.setCreator_id(2L);
        meeting.setStatus(1);
        meeting.setLocation("会议室A");
        meeting.setCover("cover.jpg");
        meeting.setCreate_time(new Date(0));
        meeting.setCreator_name("张三");
        String str = meeting.toString();
        assertTrue(str.contains("会议标题"));
        assertTrue(str.contains("内容"));
        assertTrue(str.contains("会议室A"));
        assertTrue(str.contains("cover.jpg"));
        assertTrue(str.contains("张三"));
    }
} 