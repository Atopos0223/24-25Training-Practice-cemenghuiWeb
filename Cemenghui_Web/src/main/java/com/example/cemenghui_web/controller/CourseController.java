package com.example.cemenghui_web.controller;

import com.example.cemenghui_web.entity.Course;
import com.example.cemenghui_web.mapper.CourseMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Slf4j
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
            course.setStatus("审核中");
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

    // 获取待审核课程
    @GetMapping("/list/auditing")
    public Map<String, Object> getAuditingCourses() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Course> courses = courseMapper.selectByStatus("审核中");
            courses.forEach(c -> System.out.println("封面路径: " + c.getCoverUrl()));
            result.put("success", true);
            result.put("data", courses);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败: " + e.getMessage());
        }
        return result;
    }

    // 获取已审核课程（管理用）
    @GetMapping("/list/managed")
    public Map<String, Object> getManagedCourses() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Course> courses = courseMapper.selectByStatusNot("审核中");
            courses.forEach(c -> System.out.println("封面路径: " + c.getCoverUrl()));
            result.put("success", true);
            result.put("data", courses);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getCourseDetail(@PathVariable Long id) {
        System.out.println("请求的课程ID: " + id);

        try {
            Course course = courseMapper.selectById(id);
            if (course == null) {
                System.out.println("课程不存在: " + id);
                return Map.of("success", false, "message", "课程不存在");
            }

            // 修正URL处理逻辑
            course.setCoverUrl(fixUrl(course.getCoverUrl()));
            course.setVideoUrl(fixUrl(course.getVideoUrl()));

            System.out.println("返回的课程数据: " + course);
            return Map.of("success", true, "data", course);
        } catch (Exception e) {
            System.out.println("查询异常: " + e.getMessage());
            return Map.of("success", false, "message", "服务器错误");
        }
    }

    // 独立的URL处理方法
    private String fixUrl(String url) {
        if (url == null || url.startsWith("http")) {
            return url;
        }
        // 统一处理为以/uploads/开头且不重复
        return "/uploads/" + url.replaceFirst("^/uploads/", "").replaceFirst("^uploads/", "");
    }

    @PostMapping("/{id}")
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



    @PostMapping("/audit")
    @Transactional
    public Map<String, Object> auditCourse(
            @RequestParam("id") Long id,
            @RequestParam("action") String action,
            @RequestParam(value = "comment", required = false) String comment) {

        Map<String, Object> result = new HashMap<>();
        try {
            // 1. 校验参数
            if (!"pass".equals(action) && !"reject".equals(action)) {
                throw new IllegalArgumentException("无效操作类型");
            }
            if ("reject".equals(action) && (comment == null || comment.trim().isEmpty())) {
                throw new IllegalArgumentException("拒绝原因不能为空");
            }

            // 2. 查询课程
            Course course = courseMapper.selectById(id);
            if (course == null) {
                throw new IllegalArgumentException("课程不存在");
            }

            // 3. 更新状态
            String newStatus = "pass".equals(action) ? "已发布" : "未通过";
            course.setStatus(newStatus);
            course.setAuditComment(comment);

            // 4. 保存到数据库
            int affectedRows = courseMapper.updateCourse(course);
            if (affectedRows <= 0) {
                throw new RuntimeException("更新失败");
            }

            result.put("success", true);
            result.put("message", "操作成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            log.error("审核失败: id={}, action={}", id, action, e);
        }
        return result;
    }
}