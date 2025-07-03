package com.example.cemenghui_web.service;

import com.example.cemenghui_web.entity.MeetingAttend;
import com.example.cemenghui_web.mapper.MeetingAttendMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MeetingAttendService {
    @Autowired
    private MeetingAttendMapper meetingAttendMapper;

    public void addMeetingAttend(MeetingAttend meetingAttend) {
        meetingAttendMapper.insertMeetingAttend(meetingAttend);
    }

    public java.util.List<MeetingAttend> getAllMeetingAttend() {
        return meetingAttendMapper.selectAllMeetingAttend();
    }
} 