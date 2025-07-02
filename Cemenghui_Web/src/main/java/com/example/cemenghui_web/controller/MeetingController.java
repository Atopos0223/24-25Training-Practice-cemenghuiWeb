package com.example.cemenghui_web.controller;

import com.example.cemenghui_web.entity.Meeting;
import com.example.cemenghui_web.entity.Result;
import com.example.cemenghui_web.service.MeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meeting")
@CrossOrigin
public class MeetingController {
    @Autowired
    private MeetingService meetingService;

    @PostMapping("/create")
    public Result<?> createMeeting(@RequestBody Meeting meeting) {
        try {
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
} 