package com.example.cemenghui_web.controller;

import com.example.cemenghui_web.entity.Meeting;
import com.example.cemenghui_web.entity.Result;
import com.example.cemenghui_web.service.MeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/meeting")
@CrossOrigin
public class MeetingController {
    @Autowired
    private MeetingService meetingService;

    @PostMapping("/create")
    public Result<?> createMeeting(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("start_time") String startTimeStr,
            @RequestParam("end_time") String endTimeStr,
            @RequestParam("location") String location,
            @RequestParam(value = "creator_id", required = false) Long creatorId,
            @RequestParam(value = "creator_name", required = false) String creatorName,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "cover", required = false) MultipartFile coverFile
    ) {
        try {
            com.example.cemenghui_web.entity.Meeting meeting = new com.example.cemenghui_web.entity.Meeting();
            meeting.setTitle(title);
            meeting.setContent(content);
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            meeting.setStart_time(sdf.parse(startTimeStr));
            meeting.setEnd_time(sdf.parse(endTimeStr));
            meeting.setLocation(location);
            meeting.setCreator_id(creatorId);
            meeting.setCreator_name(creatorName);
            meeting.setStatus(status);
            if (coverFile != null && !coverFile.isEmpty()) {
                String originalFilename = coverFile.getOriginalFilename();
                String suffix = originalFilename != null && originalFilename.contains(".") ? originalFilename.substring(originalFilename.lastIndexOf('.')) : "";
                String newFileName = java.util.UUID.randomUUID().toString() + suffix;

                String basePath = System.getProperty("user.dir");
                String uploadDir = basePath + java.io.File.separator + "uploads";
                java.io.File dir = new java.io.File(uploadDir);
                if (!dir.exists()) dir.mkdirs();

                java.io.File dest = new java.io.File(dir, newFileName);
                coverFile.transferTo(dest);

                meeting.setCover("uploads/" + newFileName);
            }
            meetingService.createMeeting(meeting);
            return Result.success();
        } catch (Exception e) {
            return Result.error(500, "创建会议失败: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result<List<Meeting>> getMeetingList() {
        List<Meeting> meetings = meetingService.getAllMeetings();
        return Result.success(meetings);
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> deleteMeeting(@PathVariable Long id) {
        try {
            meetingService.deleteMeeting(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(500, "删除会议失败: " + e.getMessage());
        }
    }

    @GetMapping("/detail/{id}")
    public Result<Meeting> getMeetingDetail(@PathVariable Long id) {
        Meeting meeting = meetingService.getMeetingById(id);
        System.out.println("[DEBUG] Meeting detail: " + meeting);
        if (meeting != null) {
            return Result.success(meeting);
        } else {
            return Result.error(404, "未找到该会议");
        }
    }

    @PutMapping("/update")
    public Result<?> updateMeeting(@RequestBody Meeting meeting) {
        try {
            meetingService.updateMeeting(meeting);
            return Result.success();
        } catch (Exception e) {
            return Result.error(500, "更新会议失败: " + e.getMessage());
        }
    }

    @PostMapping("/audit")
    public Result<?> auditMeeting(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        Integer status = Integer.valueOf(params.get("status").toString());
        meetingService.auditMeeting(id, status);
        return Result.success();
    }
} 