package com.example.cemenghui_web.controller;

import com.example.cemenghui_web.entity.Meeting;
import com.example.cemenghui_web.entity.Result;
import com.example.cemenghui_web.service.MeetingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class MeetingControllerTest {
    @Mock
    private MeetingService meetingService;

    @InjectMocks
    private MeetingController meetingController;

    private MultipartFile mockCoverFile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockCoverFile = new MockMultipartFile(
                "cover",
                "test.jpg",
                "image/jpeg",
                "test image".getBytes()
        );
    }

    // 创建会议成功
    @Test
    void testCreateMeetingSuccess() throws Exception {
        doNothing().when(meetingService).createMeeting(any(Meeting.class));
        Result<?> result = meetingController.createMeeting(
                "会议标题",
                "会议内容",
                "2024-07-01 10:00:00",
                "2024-07-01 12:00:00",
                "会议地点",
                1L,
                "创建者",
                1,
                mockCoverFile
        );
        assertEquals(200, result.getCode());
    }

    // 创建会议异常
    @Test
    void testCreateMeetingException() throws Exception {
        doThrow(new RuntimeException("数据库异常")).when(meetingService).createMeeting(any(Meeting.class));
        Result<?> result = meetingController.createMeeting(
                "会议标题",
                "会议内容",
                "2024-07-01 10:00:00",
                "2024-07-01 12:00:00",
                "会议地点",
                1L,
                "创建者",
                1,
                mockCoverFile
        );
        assertEquals(500, result.getCode());
        assertTrue(result.getMessage().startsWith("创建会议失败"));
    }

    // 创建会议 coverFile 为 null
    @Test
    void testCreateMeetingCoverFileNull() throws Exception {
        doNothing().when(meetingService).createMeeting(any(Meeting.class));
        Result<?> result = meetingController.createMeeting(
                "会议标题",
                "会议内容",
                "2024-07-01 10:00:00",
                "2024-07-01 12:00:00",
                "会议地点",
                1L,
                "创建者",
                1,
                null
        );
        assertEquals(200, result.getCode());
    }

    // 创建会议 coverFile 为空
    @Test
    void testCreateMeetingCoverFileEmpty() throws Exception {
        MultipartFile emptyFile = new MockMultipartFile(
                "cover",
                "empty.jpg",
                "image/jpeg",
                new byte[0]
        );
        doNothing().when(meetingService).createMeeting(any(Meeting.class));
        Result<?> result = meetingController.createMeeting(
                "会议标题",
                "会议内容",
                "2024-07-01 10:00:00",
                "2024-07-01 12:00:00",
                "会议地点",
                1L,
                "创建者",
                1,
                emptyFile
        );
        assertEquals(200, result.getCode());
    }

    // 创建会议 coverFile 无后缀
    @Test
    void testCreateMeetingCoverFileNoSuffix() throws Exception {
        MultipartFile file = new MockMultipartFile(
                "cover",
                "file",
                "image/jpeg",
                "test image".getBytes()
        );
        doNothing().when(meetingService).createMeeting(any(Meeting.class));
        Result<?> result = meetingController.createMeeting(
                "会议标题",
                "会议内容",
                "2024-07-01 10:00:00",
                "2024-07-01 12:00:00",
                "会议地点",
                1L,
                "创建者",
                1,
                file
        );
        assertEquals(200, result.getCode());
    }

    // 创建会议时间格式非法
    @Test
    void testCreateMeetingTimeFormatError() {
        Result<?> result = meetingController.createMeeting(
                "会议标题",
                "会议内容",
                "非法时间",
                "非法时间",
                "会议地点",
                1L,
                "创建者",
                1,
                mockCoverFile
        );
        assertEquals(500, result.getCode());
        assertTrue(result.getMessage().startsWith("创建会议失败"));
    }

    // 获取会议列表
    @Test
    void testGetMeetingList() {
        List<Meeting> mockList = Arrays.asList(new Meeting(), new Meeting());
        when(meetingService.getAllMeetings()).thenReturn(mockList);
        Result<List<Meeting>> result = meetingController.getMeetingList();
        assertEquals(200, result.getCode());
        assertEquals(2, result.getData().size());
    }

    // 获取会议列表返回空
    @Test
    void testGetMeetingListEmpty() {
        when(meetingService.getAllMeetings()).thenReturn(Collections.emptyList());
        Result<List<Meeting>> result = meetingController.getMeetingList();
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertTrue(result.getData().isEmpty());
    }

    // 删除会议成功
    @Test
    void testDeleteMeetingSuccess() {
        doNothing().when(meetingService).deleteMeeting(anyLong());
        Result<?> result = meetingController.deleteMeeting(1L);
        assertEquals(200, result.getCode());
    }

    // 删除会议异常
    @Test
    void testDeleteMeetingException() {
        doThrow(new RuntimeException("删除异常")).when(meetingService).deleteMeeting(anyLong());
        Result<?> result = meetingController.deleteMeeting(1L);
        assertEquals(500, result.getCode());
        assertTrue(result.getMessage().startsWith("删除会议失败"));
    }

    // 获取会议详情成功
    @Test
    void testGetMeetingDetailSuccess() {
        Meeting meeting = new Meeting();
        when(meetingService.getMeetingById(anyLong())).thenReturn(meeting);
        Result<Meeting> result = meetingController.getMeetingDetail(1L);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
    }

    // 获取会议详情失败
    @Test
    void testGetMeetingDetailFail() {
        when(meetingService.getMeetingById(anyLong())).thenReturn(null);
        Result<Meeting> result = meetingController.getMeetingDetail(1L);
        assertEquals(404, result.getCode());
        assertEquals("未找到该会议", result.getMessage());
    }

    // 更新会议成功
    @Test
    void testUpdateMeetingSuccess() {
        doNothing().when(meetingService).updateMeeting(any(Meeting.class));
        Result<?> result = meetingController.updateMeeting(new Meeting());
        assertEquals(200, result.getCode());
    }

    // 更新会议异常
    @Test
    void testUpdateMeetingException() {
        doThrow(new RuntimeException("更新异常")).when(meetingService).updateMeeting(any(Meeting.class));
        Result<?> result = meetingController.updateMeeting(new Meeting());
        assertEquals(500, result.getCode());
        assertTrue(result.getMessage().startsWith("更新会议失败"));
    }

    // 审核会议
    @Test
    void testAuditMeeting() {
        doNothing().when(meetingService).auditMeeting(anyLong(), anyInt());
        Map<String, Object> params = new HashMap<>();
        params.put("id", 1L);
        params.put("status", 1);
        Result<?> result = meetingController.auditMeeting(params);
        assertEquals(200, result.getCode());
    }

    // 审核会议参数缺失
    @Test
    void testAuditMeetingParamMissing() {
        Map<String, Object> params = new HashMap<>();
        // 缺少 id 和 status
        assertThrows(NullPointerException.class, () -> meetingController.auditMeeting(params));
    }

    // 审核会议参数类型错误
    @Test
    void testAuditMeetingParamTypeError() {
        Map<String, Object> params = new HashMap<>();
        params.put("id", "not_a_number");
        params.put("status", "not_a_number");
        assertThrows(NumberFormatException.class, () -> meetingController.auditMeeting(params));
    }

    // createMeeting 所有参数为 null
    @Test
    void testCreateMeetingAllNull() {
        Result<?> result = meetingController.createMeeting(null, null, null, null, null, null, null, null, null);
        assertEquals(500, result.getCode());
        assertTrue(result.getMessage().startsWith("创建会议失败"));
    }

    // createMeeting 所有参数为空字符串
    @Test
    void testCreateMeetingAllEmptyString() {
        Result<?> result = meetingController.createMeeting("", "", "", "", "", null, "", null, null);
        assertEquals(500, result.getCode());
        assertTrue(result.getMessage().startsWith("创建会议失败"));
    }

    // auditMeeting 参数 map 缺 id
    @Test
    void testAuditMeetingParamMissingId() {
        Map<String, Object> params = new HashMap<>();
        params.put("status", 1);
        assertThrows(NullPointerException.class, () -> meetingController.auditMeeting(params));
    }

    // auditMeeting 参数 map 缺 status
    @Test
    void testAuditMeetingParamMissingStatus() {
        Map<String, Object> params = new HashMap<>();
        params.put("id", 1);
        assertThrows(NullPointerException.class, () -> meetingController.auditMeeting(params));
    }

    // auditMeeting 参数 map 为 null
    @Test
    void testAuditMeetingParamNull() {
        assertThrows(NullPointerException.class, () -> meetingController.auditMeeting(null));
    }

    // auditMeeting 参数类型错误
    @Test
    void testAuditMeetingParamTypeError2() {
        Map<String, Object> params = new HashMap<>();
        params.put("id", "not_a_number");
        params.put("status", "not_a_number");
        assertThrows(NumberFormatException.class, () -> meetingController.auditMeeting(params));
    }

    // getMeetingDetail service 抛异常
    @Test
    void testGetMeetingDetailServiceException() {
        when(meetingService.getMeetingById(anyLong())).thenThrow(new RuntimeException("数据库异常"));
        assertThrows(RuntimeException.class, () -> meetingController.getMeetingDetail(1L));
    }

    // updateMeeting 传 null
    @Test
    void testUpdateMeetingNull() {
        Result<?> result = meetingController.updateMeeting(null);
        assertEquals(200, result.getCode());
    }

    // updateMeeting service 抛异常
    @Test
    void testUpdateMeetingServiceException() {
        doThrow(new RuntimeException("数据库异常")).when(meetingService).updateMeeting(any(Meeting.class));
        Result<?> result = meetingController.updateMeeting(new Meeting());
        assertEquals(500, result.getCode());
        assertTrue(result.getMessage().startsWith("更新会议失败"));
    }

    // deleteMeeting 传 null
    @Test
    void testDeleteMeetingNull() {
        Result<?> result = meetingController.deleteMeeting(null);
        assertEquals(200, result.getCode());
    }

    // deleteMeeting service 抛异常
    @Test
    void testDeleteMeetingServiceException2() {
        doThrow(new RuntimeException("数据库异常")).when(meetingService).deleteMeeting(anyLong());
        Result<?> result = meetingController.deleteMeeting(2L);
        assertEquals(500, result.getCode());
        assertTrue(result.getMessage().startsWith("删除会议失败"));
    }

    // createMeeting coverFile.getOriginalFilename() 返回 null
    @Test
    void testCreateMeetingCoverFileOriginalFilenameNull() throws Exception {
        MultipartFile cover = mock(MultipartFile.class);
        when(cover.isEmpty()).thenReturn(false);
        when(cover.getOriginalFilename()).thenReturn(null);
        doNothing().when(cover).transferTo(any(java.io.File.class));
        doNothing().when(meetingService).createMeeting(any(Meeting.class));
        Result<?> result = meetingController.createMeeting(
                "会议标题", "会议内容", "2024-07-01 10:00:00", "2024-07-01 12:00:00",
                "会议地点", 1L, "创建者", 1, cover
        );
        assertEquals(200, result.getCode());
    }

    // createMeeting coverFile.transferTo 抛异常
    @Test
    void testCreateMeetingCoverFileTransferToException() throws Exception {
        MultipartFile cover = mock(MultipartFile.class);
        when(cover.isEmpty()).thenReturn(false);
        when(cover.getOriginalFilename()).thenReturn("cover.jpg");
        doThrow(new RuntimeException("IO异常")).when(cover).transferTo(any(java.io.File.class));
        Result<?> result = meetingController.createMeeting(
                "会议标题", "会议内容", "2024-07-01 10:00:00", "2024-07-01 12:00:00",
                "会议地点", 1L, "创建者", 1, cover
        );
        assertEquals(500, result.getCode());
        assertTrue(result.getMessage().contains("创建会议失败"));
    }

    // createMeeting coverFile.isEmpty() 抛异常
    @Test
    void testCreateMeetingCoverFileIsEmptyThrows() throws Exception {
        MultipartFile cover = mock(MultipartFile.class);
        when(cover.isEmpty()).thenThrow(new RuntimeException("isEmpty异常"));
        Result<?> result = meetingController.createMeeting(
                "会议标题", "会议内容", "2024-07-01 10:00:00", "2024-07-01 12:00:00",
                "会议地点", 1L, "创建者", 1, cover
        );
        assertEquals(500, result.getCode());
        assertTrue(result.getMessage().contains("创建会议失败"));
    }
} 