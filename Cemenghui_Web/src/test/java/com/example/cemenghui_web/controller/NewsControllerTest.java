package com.example.cemenghui_web.controller;

import com.example.cemenghui_web.entity.News;
import com.example.cemenghui_web.entity.Result;
import com.example.cemenghui_web.service.NewsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class NewsControllerTest {
    @Mock
    private NewsService newsService;

    @InjectMocks
    private NewsController newsController;

    private MultipartFile mockFile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockFile = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image".getBytes()
        );
    }

    // 获取新闻列表
    @Test
    void testGetAllNews() {
        List<News> mockList = Arrays.asList(new News(), new News());
        when(newsService.getAllNews()).thenReturn(mockList);
        Result<List<News>> result = newsController.getAllNews();
        assertEquals(200, result.getCode());
        assertEquals(2, result.getData().size());
    }

    // 获取新闻列表返回空
    @Test
    void testGetAllNewsEmpty() {
        when(newsService.getAllNews()).thenReturn(Collections.emptyList());
        Result<List<News>> result = newsController.getAllNews();
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertTrue(result.getData().isEmpty());
    }

    // 新增新闻成功
    @Test
    void testAddNewsSuccess() {
        when(newsService.insertNews(any(News.class))).thenReturn(1);
        Result<?> result = newsController.addNews(new News());
        assertEquals(200, result.getCode());
    }

    // 新增新闻失败
    @Test
    void testAddNewsFail() {
        when(newsService.insertNews(any(News.class))).thenReturn(0);
        Result<?> result = newsController.addNews(new News());
        assertEquals(500, result.getCode());
        assertEquals("发布动态失败", result.getMessage());
    }

    // 上传文件成功
    @Test
    void testUploadSuccess() {
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image".getBytes()
        );
        Result<?> result = newsController.upload(file);
        assertEquals(200, result.getCode());
        assertTrue(result.getData() instanceof String);
    }

    // 上传文件为空
    @Test
    void testUploadEmptyFile() {
        MultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.jpg",
                "image/jpeg",
                new byte[0]
        );
        Result<?> result = newsController.upload(emptyFile);
        assertEquals(400, result.getCode());
        assertEquals("文件为空", result.getMessage());
    }

    // 上传文件异常
    @Test
    void testUploadException() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        doThrow(new RuntimeException("IO错误")).when(file).transferTo(any(java.io.File.class));
        Result<?> result = newsController.upload(file);
        assertEquals(500, result.getCode());
        assertTrue(result.getMessage().startsWith("上传失败"));
    }

    // 上传文件.isEmpty() 抛异常
    @Test
    void testUploadFileIsEmptyThrows() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenThrow(new RuntimeException("isEmpty异常"));
        Result<?> result = newsController.upload(file);
        assertEquals(500, result.getCode());
        assertTrue(result.getMessage().contains("上传失败"));
    }

    // 获取新闻详情成功
    @Test
    void testGetNewsDetailSuccess() {
        News news = new News();
        when(newsService.getNewsById(anyInt())).thenReturn(news);
        Result<News> result = newsController.getNewsDetail(1);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
    }

    // 获取新闻详情失败
    @Test
    void testGetNewsDetailFail() {
        when(newsService.getNewsById(anyInt())).thenReturn(null);
        Result<News> result = newsController.getNewsDetail(1);
        assertEquals(404, result.getCode());
        assertEquals("未找到该新闻", result.getMessage());
    }

    // 删除新闻成功
    @Test
    void testDeleteNewsSuccess() {
        when(newsService.deleteNewsById(anyLong())).thenReturn(1);
        Result<?> result = newsController.deleteNews(1L);
        assertEquals(200, result.getCode());
    }

    // 删除新闻失败
    @Test
    void testDeleteNewsFail() {
        when(newsService.deleteNewsById(anyLong())).thenReturn(0);
        Result<?> result = newsController.deleteNews(1L);
        assertEquals(404, result.getCode());
        assertEquals("删除失败，未找到该新闻", result.getMessage());
    }

    // 更新新闻成功
    @Test
    void testUpdateNewsSuccess() {
        when(newsService.updateNews(any(News.class))).thenReturn(1);
        Result<?> result = newsController.updateNews(new News());
        assertEquals(200, result.getCode());
    }

    // 更新新闻失败
    @Test
    void testUpdateNewsFail() {
        when(newsService.updateNews(any(News.class))).thenReturn(0);
        Result<?> result = newsController.updateNews(new News());
        assertEquals(404, result.getCode());
        assertEquals("更新失败，未找到该新闻", result.getMessage());
    }

    // 审核新闻成功
    @Test
    void testAuditNewsSuccess() {
        when(newsService.auditNews(anyInt(), anyInt())).thenReturn(1);
        Map<String, Object> params = new HashMap<>();
        params.put("id", 1);
        params.put("status", 1);
        Result<?> result = newsController.auditNews(params);
        assertEquals(200, result.getCode());
    }

    // 审核新闻失败
    @Test
    void testAuditNewsFail() {
        when(newsService.auditNews(anyInt(), anyInt())).thenReturn(0);
        Map<String, Object> params = new HashMap<>();
        params.put("id", 1);
        params.put("status", 1);
        Result<?> result = newsController.auditNews(params);
        assertEquals(500, result.getCode());
        assertEquals("审核失败", result.getMessage());
    }

    // 审核新闻参数缺失
    @Test
    void testAuditNewsParamMissing() {
        Map<String, Object> params = new HashMap<>();
        // 缺少 id 和 status
        when(newsService.auditNews(isNull(), isNull())).thenReturn(0);
        Result<?> result = newsController.auditNews(params);
        assertEquals(500, result.getCode());
        assertEquals("审核失败", result.getMessage());
    }

    // 审核新闻参数类型错误
    @Test
    void testAuditNewsParamTypeError() {
        Map<String, Object> params = new HashMap<>();
        params.put("id", "not_a_number");
        params.put("status", "not_a_number");
        assertThrows(ClassCastException.class, () -> newsController.auditNews(params));
    }
} 