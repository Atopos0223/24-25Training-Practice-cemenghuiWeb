package com.example.cemenghui_web.controller;

import com.example.cemenghui_web.entity.MeetingAttend;
import com.example.cemenghui_web.entity.Result;
import com.example.cemenghui_web.service.MeetingAttendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/meeting-attend")
@CrossOrigin
public class MeetingAttendController {
    @Autowired
    private MeetingAttendService meetingAttendService;

    @PostMapping("/add")
    public Result<?> addMeetingAttend(@RequestBody MeetingAttend meetingAttend) {
        try {
            meetingAttendService.addMeetingAttend(meetingAttend);
            return Result.success();
        } catch (Exception e) {
            return Result.error(500, "添加签到信息失败: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result<java.util.List<MeetingAttend>> getAllMeetingAttend() {
        java.util.List<MeetingAttend> list = meetingAttendService.getAllMeetingAttend();
        return Result.success(list);
    }
} 