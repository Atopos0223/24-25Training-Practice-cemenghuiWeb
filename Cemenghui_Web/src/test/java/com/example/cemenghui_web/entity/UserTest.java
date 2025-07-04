package com.example.cemenghui_web.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.Timestamp;

public class UserTest {
    @Test
    void testAllGetterSetter() {
        User user = new User();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        user.setId(1);
        user.setCompany("公司");
        user.setUsername("测试用户");
        user.setPassword("123456");
        user.setNickname("昵称");
        user.setPhone("12345678901");
        user.setEmail("test@example.com");
        user.setGender(1);
        user.setStatus(1);
        user.setIs_super(1);
        user.setCreate_time(now);
        assertEquals(1, user.getId());
        assertEquals("公司", user.getCompany());
        assertEquals("测试用户", user.getUsername());
        assertEquals("123456", user.getPassword());
        assertEquals("昵称", user.getNickname());
        assertEquals("12345678901", user.getPhone());
        assertEquals("test@example.com", user.getEmail());
        assertEquals(1, user.getGender());
        assertEquals(1, user.getStatus());
        assertEquals(1, user.getIs_super());
        assertEquals(now, user.getCreate_time());
    }

    @Test
    void testToString() {
        User user = new User();
        user.setId(1);
        user.setCompany("公司");
        user.setUsername("测试用户");
        user.setPassword("123456");
        user.setNickname("昵称");
        user.setPhone("12345678901");
        user.setEmail("test@example.com");
        user.setGender(1);
        user.setStatus(1);
        user.setIs_super(1);
        user.setCreate_time(new Timestamp(0));
        String str = user.toString();
        assertTrue(str.contains("测试用户"));
        assertTrue(str.contains("公司"));
        assertTrue(str.contains("昵称"));
        assertTrue(str.contains("12345678901"));
        assertTrue(str.contains("test@example.com"));
    }
} 