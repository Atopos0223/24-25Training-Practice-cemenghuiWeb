package com.example.cemenghui_web.controller;

import com.example.cemenghui_web.entity.News;
import com.example.cemenghui_web.entity.Result;
import com.example.cemenghui_web.entity.User;
import com.example.cemenghui_web.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.interfaces.RSAKey;
import java.util.List;

@RestController
@CrossOrigin
public class NewsController {
    @Autowired
    NewsService newsService;

    @PostMapping("/addNews")
    public Result<Boolean> addNews(@RequestBody News news){
        boolean flag = newsService.addNews(news);
        if(flag){
            return Result.success(flag);
        }else{
            return Result.error(404,"新增失败");
        }
    }

    @PostMapping("/getAllNews")
    public Result<List> getAllNews(){
        List<News> allNews = newsService.getAllNews();
        if(allNews != null){
            return Result.success(allNews);
        }else{
            return Result.error(404,"获取失败");
        }
    }
}
