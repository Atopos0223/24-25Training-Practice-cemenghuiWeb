package com.example.cemenghui_web.service;

import com.example.cemenghui_web.entity.News;
import com.example.cemenghui_web.mapper.NewsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsService {
    @Autowired
    private NewsMapper newsMapper;

    public int insertNews(News news) {
        return newsMapper.insertNews(news);
    }

    public List<News> getAllNews() {
        return newsMapper.getAllNews();
    }

    public News getNewsById(Integer id) {
        return newsMapper.getNewsById(id);
    }

    public int deleteNewsById(Long id) {
        return newsMapper.deleteNewsById(id);
    }

    public int updateNews(News news) {
        return newsMapper.updateNews(news);
    }

    public int auditNews(Integer id, Integer status) {
        return newsMapper.auditNews(id, status);
    }
} 