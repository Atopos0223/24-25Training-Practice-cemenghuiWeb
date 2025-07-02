package com.example.cemenghui_web.service;

import com.example.cemenghui_web.entity.Meeting;
import com.example.cemenghui_web.mapper.MeetingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MeetingService {
    @Autowired
    private MeetingMapper meetingMapper;

    public void createMeeting(Meeting meeting) {
        if (meeting.getCreateTime() == null) {
            meeting.setCreateTime(new java.util.Date());
        }
        // status 1 表示未审核
        meeting.setStatus(1);
        meetingMapper.insertMeeting(meeting);
    }

    public List<Meeting> getAllMeetings() {
        return meetingMapper.selectAllMeetings();
    }

    public void deleteMeeting(Long id) {
        meetingMapper.deleteMeeting(id);
    }

    public Meeting getMeetingById(Long id) {
        return meetingMapper.selectMeetingWithCreatorNameById(id);
    }

    public void updateMeeting(Meeting meeting) {
        meetingMapper.updateMeeting(meeting);
    }
} 