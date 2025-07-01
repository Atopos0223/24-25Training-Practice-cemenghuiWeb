package com.example.cemenghui_web.service;

import com.example.cemenghui_web.entity.News;
import com.example.cemenghui_web.mapper.NewsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.util.List;

@Service
public class NewsService {
    @Autowired
    NewsMapper newsMapper;

    public boolean addNews(News news){
        return newsMapper.addNews(news);
    }

    public List<News> getAllNews(){
        return newsMapper.getAllNews();
    }
}
