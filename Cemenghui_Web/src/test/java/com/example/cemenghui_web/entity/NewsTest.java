package com.example.cemenghui_web.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.Timestamp;

public class NewsTest {
    @Test
    void testAllGetterSetter() {
        News news = new News();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        news.setId(1);
        news.setAuthor_id(2);
        news.setTitle("测试新闻");
        news.setImage("img.jpg");
        news.setSummary("摘要");
        news.setContent("内容");
        news.setStatus(1);
        news.setCreate_time(now);
        news.setUpdate_time(now);
        assertEquals(1, news.getId());
        assertEquals(2, news.getAuthor_id());
        assertEquals("测试新闻", news.getTitle());
        assertEquals("img.jpg", news.getImage());
        assertEquals("摘要", news.getSummary());
        assertEquals("内容", news.getContent());
        assertEquals(1, news.getStatus());
        assertEquals(now, news.getCreate_time());
        assertEquals(now, news.getUpdate_time());
    }
} 