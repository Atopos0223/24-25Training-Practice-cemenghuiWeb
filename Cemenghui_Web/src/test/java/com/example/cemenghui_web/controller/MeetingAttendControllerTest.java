package com.example.cemenghui_web.controller;

import com.example.cemenghui_web.entity.MeetingAttend;
import com.example.cemenghui_web.entity.Result;
import com.example.cemenghui_web.service.MeetingAttendService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class MeetingAttendControllerTest {
    @Mock
    private MeetingAttendService meetingAttendService;

    @InjectMocks
    private MeetingAttendController meetingAttendController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 添加签到成功
    @Test
    void testAddMeetingAttendSuccess() {
        doNothing().when(meetingAttendService).addMeetingAttend(any(MeetingAttend.class));
        Result<?> result = meetingAttendController.addMeetingAttend(new MeetingAttend());
        assertEquals(200, result.getCode());
    }

    // 添加签到异常
    @Test
    void testAddMeetingAttendException() {
        doThrow(new RuntimeException("数据库异常")).when(meetingAttendService).addMeetingAttend(any(MeetingAttend.class));
        Result<?> result = meetingAttendController.addMeetingAttend(new MeetingAttend());
        assertEquals(500, result.getCode());
        assertTrue(result.getMessage().startsWith("添加签到信息失败"));
    }

    // 查询签到列表
    @Test
    void testGetAllMeetingAttend() {
        List<MeetingAttend> mockList = Arrays.asList(new MeetingAttend(), new MeetingAttend());
        when(meetingAttendService.getAllMeetingAttend()).thenReturn(mockList);
        Result<List<MeetingAttend>> result = meetingAttendController.getAllMeetingAttend();
        assertEquals(200, result.getCode());
        assertEquals(2, result.getData().size());
    }

    // 查询签到列表返回空
    @Test
    void testGetAllMeetingAttendEmpty() {
        when(meetingAttendService.getAllMeetingAttend()).thenReturn(Collections.emptyList());
        Result<List<MeetingAttend>> result = meetingAttendController.getAllMeetingAttend();
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertTrue(result.getData().isEmpty());
    }

    // 添加签到传 null
    @Test
    void testAddMeetingAttendNull() {
        doNothing().when(meetingAttendService).addMeetingAttend(isNull());
        Result<?> result = meetingAttendController.addMeetingAttend(null);
        assertEquals(200, result.getCode());
    }
} 