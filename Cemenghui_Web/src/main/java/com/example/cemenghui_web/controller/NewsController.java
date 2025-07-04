package com.example.cemenghui_web.controller;

import com.example.cemenghui_web.entity.News;
import com.example.cemenghui_web.entity.Result;
import com.example.cemenghui_web.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/news")
public class NewsController {
    @Autowired
    private NewsService newsService;

    // 新增获取新闻列表接口，处理 /news/all 请求
    @GetMapping("/all")
    public Result<List<News>> getAllNews() {
        List<News> newsList = newsService.getAllNews();
        return Result.success(newsList);
    }

    @PostMapping("/add")
    public Result<?> addNews(@RequestBody News news) {
        int result = newsService.insertNews(news);
        if (result > 0) {
            return Result.success();
        } else {
            return Result.error(500, "发布动态失败");
        }
    }

    @PostMapping("/upload")
    public Result<?> upload(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.error(400, "文件为空");
            }
            try {
                // 保存路径，可根据实际情况修改
                String uploadDir = System.getProperty("user.dir") + "/uploads/";
                java.io.File dir = new java.io.File(uploadDir);
                if (!dir.exists()) dir.mkdirs();
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                java.io.File dest = new java.io.File(uploadDir + fileName);
                file.transferTo(dest);
                // 假设前端可通过 /uploads/ 访问图片
                String url = "/uploads/" + fileName;
                return Result.success(url);
            } catch (Exception e) {
                return Result.error(500, "上传失败: " + e.getMessage());
            }
        } catch (Exception e) {
            return Result.error(500, "上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/detail/{id}")
    public Result<News> getNewsDetail(@PathVariable Integer id) {
        News news = newsService.getNewsById(id);
        if (news != null) {
            return Result.success(news);
        } else {
            return Result.error(404, "未找到该新闻");
        }
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> deleteNews(@PathVariable Long id) {
        int result = newsService.deleteNewsById(id);
        if (result > 0) {
            return Result.success();
        } else {
            return Result.error(404, "删除失败，未找到该新闻");
        }
    }

    @PostMapping("/update")
    public Result<?> updateNews(@RequestBody News news) {
        int result = newsService.updateNews(news);
        if (result > 0) {
            return Result.success();
        } else {
            return Result.error(404, "更新失败，未找到该新闻");
        }
    }

    @PostMapping("/audit")
    public Result<?> auditNews(@RequestBody Map<String, Object> params) {
        Integer id = (Integer) params.get("id");
        Integer status = (Integer) params.get("status");
        int result = newsService.auditNews(id, status);
        if (result > 0) {
            return Result.success();
        } else {
            return Result.error(500, "审核失败");
        }
    }
} 