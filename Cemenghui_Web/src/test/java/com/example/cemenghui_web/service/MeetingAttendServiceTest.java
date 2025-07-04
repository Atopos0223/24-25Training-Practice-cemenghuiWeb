package com.example.cemenghui_web.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.*;
import com.example.cemenghui_web.entity.MeetingAttend;
import com.example.cemenghui_web.mapper.MeetingAttendMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;

@SpringBootTest
public class MeetingAttendServiceTest {
    @Mock
    private MeetingAttendMapper meetingAttendMapper;

    @InjectMocks
    private MeetingAttendService meetingAttendService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testServiceNotNull() {
        assertNotNull(meetingAttendService);
    }

    @Test
    void testAddMeetingAttend() {
        MeetingAttend meetingAttend = new MeetingAttend();
        doNothing().when(meetingAttendMapper).insertMeetingAttend(meetingAttend);
        meetingAttendService.addMeetingAttend(meetingAttend);
        verify(meetingAttendMapper, times(1)).insertMeetingAttend(meetingAttend);
    }

    @Test
    void testGetAllMeetingAttend() {
        MeetingAttend m1 = new MeetingAttend(); MeetingAttend m2 = new MeetingAttend();
        List<MeetingAttend> expected = Arrays.asList(m1, m2);
        when(meetingAttendMapper.selectAllMeetingAttend()).thenReturn(expected);
        List<MeetingAttend> actual = meetingAttendService.getAllMeetingAttend();
        assertEquals(2, actual.size());
        verify(meetingAttendMapper, times(1)).selectAllMeetingAttend();
    }
} 