package com.example.cemenghui_web.service;

import com.example.cemenghui_web.entity.Meeting;
import com.example.cemenghui_web.mapper.MeetingMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MeetingServiceTest {
    @Mock
    private MeetingMapper meetingMapper;

    @InjectMocks
    private MeetingService meetingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateMeeting() {
        Meeting meeting = new Meeting();
        meetingService.createMeeting(meeting);
        verify(meetingMapper, times(1)).insertMeeting(meeting);
    }

    @Test
    void testGetAllMeetings() {
        Meeting m1 = new Meeting(); m1.setId(1L); m1.setTitle("A");
        Meeting m2 = new Meeting(); m2.setId(2L); m2.setTitle("B");
        when(meetingMapper.selectAllMeetings()).thenReturn(Arrays.asList(m1, m2));
        List<Meeting> list = meetingService.getAllMeetings();
        assertEquals(2, list.size());
        assertEquals("A", list.get(0).getTitle());
    }

    @Test
    void testDeleteMeeting() {
        meetingService.deleteMeeting(123L);
        verify(meetingMapper, times(1)).deleteMeeting(123L);
    }

    @Test
    void testGetMeetingById() {
        Meeting m = new Meeting(); m.setId(5L); m.setTitle("Detail");
        when(meetingMapper.selectMeetingWithCreatorNameById(5L)).thenReturn(m);
        Meeting result = meetingService.getMeetingById(5L);
        assertNotNull(result);
        assertEquals("Detail", result.getTitle());
    }

    @Test
    void testUpdateMeeting() {
        Meeting m = new Meeting(); m.setId(7L);
        meetingService.updateMeeting(m);
        verify(meetingMapper, times(1)).updateMeeting(m);
    }

    @Test
    void testAuditMeeting() {
        meetingService.auditMeeting(8L, 2);
        verify(meetingMapper, times(1)).updateMeetingStatus(8L, 2);
    }

    @Test
    void testCreateMeetingWithCreateTime() {
        Meeting meeting = new Meeting();
        java.util.Date now = new java.util.Date();
        meeting.setCreateTime(now);
        meetingService.createMeeting(meeting);
        verify(meetingMapper, times(1)).insertMeeting(meeting);
        assertEquals(now, meeting.getCreateTime());
        assertEquals(1, meeting.getStatus());
    }
} 