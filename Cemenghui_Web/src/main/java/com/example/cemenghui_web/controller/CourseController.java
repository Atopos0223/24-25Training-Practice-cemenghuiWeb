package com.example.cemenghui_web.controller;

import com.example.cemenghui_web.entity.Course;
import com.example.cemenghui_web.mapper.CourseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/api/course")
public class CourseController {

    @Autowired
    private CourseMapper courseMapper;

    private static final String UPLOAD_DIR = "/var/uploads/";  // 实际存储路径
    private static final String UPLOAD_PATH = "/uploads/";     // URL访问路径

    @PostMapping("/add")
    public Map<String, Object> addCourse(
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam("cover") MultipartFile cover,
            @RequestParam(value = "intro", required = false) String intro,
            @RequestParam(value = "video", required = false) MultipartFile video,
            @RequestParam(value = "status", defaultValue = "已发布") String status) {

        Map<String, Object> result = new HashMap<>();
        try {
            // 1. 验证必要参数
            if (cover.isEmpty()) {
                throw new IllegalArgumentException("课程封面不能为空");
            }

            // 2. 确保上传目录存在
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 3. 保存文件
            String coverName = saveFile(cover);
            String videoName = (video != null && !video.isEmpty()) ? saveFile(video) : null;

            // 4. 构建数据库实体
            Course course = new Course();
            course.setTitle(title);
            course.setAuthor(author);
            course.setCoverUrl("/uploads/" + coverName);
            course.setIntro(intro);
            course.setVideoUrl(videoName != null ? "/uploads/" + videoName : null);
            course.setStatus(status);
            course.setCreateTime(new Date());

            // 5. 写入数据库
            int affectedRows = courseMapper.insertCourse(course);
            if (affectedRows <= 0) {
                throw new RuntimeException("数据库插入失败");
            }

            // 6. 返回结果
            result.put("success", true);
            result.put("data", course);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            cleanUpFailedUploads(); // 清理失败的上传文件
        }
        return result;
    }

    @GetMapping("/list")
    public Map<String, Object> getCourseList() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Course> courses = courseMapper.selectAllCourses();
            courses.forEach(c -> System.out.println("封面路径: " + c.getCoverUrl()));
            result.put("success", true);
            result.put("data", courses);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败: " + e.getMessage());
        }
        return result;
    }

    //=== 私有方法 ===//
    private String saveFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR + fileName);
        file.transferTo(filePath);
        return fileName;
    }

    private void cleanUpFailedUploads() {
        // 实现失败时清理已上传文件的逻辑
    }

    @GetMapping("/{id}")
    public Map<String, Object> getCourseDetail(@PathVariable Long id) {
        System.out.println("请求的课程ID: " + id);

        Course course = courseMapper.selectById(id);
        System.out.println("数据库查询结果: " + course); // 现在会打印详细字段

        if (course == null) {
            System.out.println("课程不存在: " + id);
            return Map.of("success", false, "message", "课程不存在");
        }

        // 确保封面URL格式正确
        if (course.getCoverUrl() != null && !course.getCoverUrl().startsWith("http")) {
            course.setCoverUrl("/uploads/" + course.getCoverUrl().replaceFirst("^/uploads/", ""));
        }
        // 确保视频URL格式正确
        if (course.getVideoUrl() != null && !course.getVideoUrl().startsWith("http")) {
            course.setVideoUrl("/uploads/" + course.getVideoUrl().replaceFirst("^/uploads/", ""));
        }

        return Map.of("success", true, "data", course);
    }

    @PostMapping("/edit/{id}")
    public Map<String, Object> editCourse(
            @PathVariable Long id,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "author", required = false) String author,
            @RequestParam(value = "cover", required = false) MultipartFile cover,
            @RequestParam(value = "intro", required = false) String intro,
            @RequestParam(value = "video", required = false) MultipartFile video,
            @RequestParam(value = "status", required = false) String status) {

        Map<String, Object> result = new HashMap<>();
        try {
            // 1. 获取现有课程
            Course existingCourse = courseMapper.selectById(id);
            if (existingCourse == null) {
                throw new IllegalArgumentException("课程不存在");
            }

            // 2. 更新字段（非空才更新）
            if (title != null) existingCourse.setTitle(title);
            if (author != null) existingCourse.setAuthor(author);
            if (intro != null) existingCourse.setIntro(intro);
            if (status != null) existingCourse.setStatus(status);

            // 3. 处理封面更新
            if (cover != null && !cover.isEmpty()) {
                String coverName = saveFile(cover);
                existingCourse.setCoverUrl("/uploads/" + coverName);
            }

            // 4. 处理视频更新
            if (video != null && !video.isEmpty()) {
                String videoName = saveFile(video);
                existingCourse.setVideoUrl("/uploads/" + videoName);
            }

            // 5. 更新数据库
            int affectedRows = courseMapper.updateCourse(existingCourse);
            if (affectedRows <= 0) {
                throw new RuntimeException("数据库更新失败");
            }

            result.put("success", true);
            result.put("data", existingCourse);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteCourse(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            int affectedRows = courseMapper.deleteById(id);
            if (affectedRows <= 0) {
                throw new RuntimeException("删除失败，课程可能不存在");
            }
            result.put("success", true);
            result.put("message", "删除成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除失败: " + e.getMessage());
        }
        return result;
    }
}