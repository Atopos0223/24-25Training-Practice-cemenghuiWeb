package com.example.cemenghui_web.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.Timestamp;

public class MeetingAttendTest {
    @Test
    void testAllGetterSetter() {
        MeetingAttend attend = new MeetingAttend();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        attend.setId(1L);
        attend.setUnit("单位");
        attend.setName("张三");
        attend.setGender("男");
        attend.setPhone("12345678901");
        attend.setEmail("test@example.com");
        attend.setArrivalTime(now);
        assertEquals(1L, attend.getId());
        assertEquals("单位", attend.getUnit());
        assertEquals("张三", attend.getName());
        assertEquals("男", attend.getGender());
        assertEquals("12345678901", attend.getPhone());
        assertEquals("test@example.com", attend.getEmail());
        assertEquals(now, attend.getArrivalTime());
    }
} 