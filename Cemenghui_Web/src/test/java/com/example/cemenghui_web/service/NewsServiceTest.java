package com.example.cemenghui_web.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.*;
import com.example.cemenghui_web.entity.News;
import com.example.cemenghui_web.mapper.NewsMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class NewsServiceTest {
    @Autowired
    private NewsService newsService;

    @Mock
    private NewsMapper newsMapper;

    @InjectMocks
    private NewsService newsServiceMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testServiceNotNull() {
        assertNotNull(newsService);
    }

    @Test
    void testInsertNews() {
        News news = new News();
        when(newsMapper.insertNews(news)).thenReturn(1);
        int result = newsServiceMock.insertNews(news);
        assertEquals(1, result);
        verify(newsMapper, times(1)).insertNews(news);
    }

    @Test
    void testGetAllNews() {
        News n1 = new News(); News n2 = new News();
        List<News> expected = Arrays.asList(n1, n2);
        when(newsMapper.getAllNews()).thenReturn(expected);
        List<News> actual = newsServiceMock.getAllNews();
        assertEquals(2, actual.size());
        verify(newsMapper, times(1)).getAllNews();
    }

    @Test
    void testGetNewsById() {
        News news = new News();
        when(newsMapper.getNewsById(1)).thenReturn(news);
        News result = newsServiceMock.getNewsById(1);
        assertEquals(news, result);
        verify(newsMapper, times(1)).getNewsById(1);
    }

    @Test
    void testDeleteNewsById() {
        when(newsMapper.deleteNewsById(1L)).thenReturn(1);
        int result = newsServiceMock.deleteNewsById(1L);
        assertEquals(1, result);
        verify(newsMapper, times(1)).deleteNewsById(1L);
    }

    @Test
    void testUpdateNews() {
        News news = new News();
        when(newsMapper.updateNews(news)).thenReturn(1);
        int result = newsServiceMock.updateNews(news);
        assertEquals(1, result);
        verify(newsMapper, times(1)).updateNews(news);
    }

    @Test
    void testAuditNews() {
        when(newsMapper.auditNews(1, 2)).thenReturn(1);
        int result = newsServiceMock.auditNews(1, 2);
        assertEquals(1, result);
        verify(newsMapper, times(1)).auditNews(1, 2);
    }
} 