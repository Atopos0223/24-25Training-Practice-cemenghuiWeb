package com.example.cemenghui_web.controller;

import com.example.cemenghui_web.entity.Result;
import com.example.cemenghui_web.entity.User;
import com.example.cemenghui_web.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 登录成功
    @Test
    void testLoginSuccess() {
        User user = new User();
        when(userService.Login(any(User.class))).thenReturn(user);
        Result<User> result = userController.login(user);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
    }

    // 登录失败
    @Test
    void testLoginFail() {
        when(userService.Login(any(User.class))).thenReturn(null);
        Result<User> result = userController.login(new User());
        assertEquals(404, result.getCode());
        assertNull(result.getData());
    }

    // 注册成功
    @Test
    void testRegisterSuccess() {
        User user = new User();
        user.setUsername("test");
        when(userService.findByUsername(anyString())).thenReturn(null);
        doNothing().when(userService).register(any(User.class));
        Result<?> result = userController.register(user);
        assertEquals(200, result.getCode());
    }

    // 注册用户名已存在
    @Test
    void testRegisterUsernameExists() {
        User user = new User();
        user.setUsername("test");
        when(userService.findByUsername(anyString())).thenReturn(new User());
        Result<?> result = userController.register(user);
        assertEquals(400, result.getCode());
        assertEquals("用户名已存在", result.getMessage());
    }

    // 更新用户信息成功
    @Test
    void testUpdateUserSuccess() {
        doNothing().when(userService).updateUser(any(User.class));
        Result<?> result = userController.updateUser(new User());
        assertEquals(200, result.getCode());
    }

    // 更新用户信息失败
    @Test
    void testUpdateUserFail() {
        doThrow(new RuntimeException("数据库异常")).when(userService).updateUser(any(User.class));
        Result<?> result = userController.updateUser(new User());
        assertEquals(500, result.getCode());
        assertTrue(result.getMessage().contains("更新用户信息失败"));
    }

    // 修改密码成功
    @Test
    void testUpdatePasswordSuccess() {
        Map<String, String> req = new HashMap<>();
        req.put("username", "test");
        req.put("oldPassword", "old");
        req.put("newPassword", "new");
        when(userService.updatePassword(anyString(), anyString(), anyString())).thenReturn(true);
        Result<?> result = userController.updatePassword(req);
        assertEquals(200, result.getCode());
    }

    // 修改密码失败（旧密码错误）
    @Test
    void testUpdatePasswordFail() {
        Map<String, String> req = new HashMap<>();
        req.put("username", "test");
        req.put("oldPassword", "old");
        req.put("newPassword", "new");
        when(userService.updatePassword(anyString(), anyString(), anyString())).thenReturn(false);
        Result<?> result = userController.updatePassword(req);
        assertEquals(400, result.getCode());
        assertEquals("旧密码错误或用户不存在", result.getMessage());
    }

    // 修改密码异常
    @Test
    void testUpdatePasswordException() {
        Map<String, String> req = new HashMap<>();
        req.put("username", "u");
        req.put("oldPassword", "o");
        req.put("newPassword", "n");
        when(userService.updatePassword(anyString(), anyString(), anyString()))
            .thenThrow(new RuntimeException("异常"));
        Result<?> result = userController.updatePassword(req);
        assertEquals(500, result.getCode());
        assertEquals("修改密码失败: 异常", result.getMessage());
    }

    // 找回密码成功
    @Test
    void testFindPasswordSuccess() {
        Map<String, String> req = new HashMap<>();
        req.put("username", "test");
        req.put("phone", "123");
        req.put("newPassword", "new");
        when(userService.findPassword(anyString(), anyString(), anyString())).thenReturn(true);
        Result<?> result = userController.findPassword(req);
        assertEquals(200, result.getCode());
    }

    // 找回密码失败
    @Test
    void testFindPasswordFail() {
        Map<String, String> req = new HashMap<>();
        when(userService.findPassword(anyString(), anyString(), anyString())).thenReturn(false);
        Result<?> result = userController.findPassword(req);
        assertEquals(400, result.getCode());
        assertEquals("用户名或手机号不正确", result.getMessage());
    }

    // 找回密码异常
    @Test
    void testFindPasswordException() {
        Map<String, String> req = new HashMap<>();
        req.put("username", "u");
        req.put("phone", "p");
        req.put("newPassword", "n");
        when(userService.findPassword(anyString(), anyString(), anyString()))
            .thenThrow(new RuntimeException("异常"));
        Result<?> result = userController.findPassword(req);
        assertEquals(500, result.getCode());
        assertEquals("找回密码失败: 异常", result.getMessage());
    }

    // 新增用户（管理员）
    @Test
    void testAddUser() {
        when(userService.insertUser(any(User.class))).thenReturn(1);
        Result<?> result = userController.addUser(new User());
        assertEquals(200, result.getCode());
    }

    // 查询用户列表
    @Test
    void testUserList() {
        when(userService.selectUserList(any(), any(), any(), any(), any(), any())).thenReturn(Arrays.asList(new User(), new User()));
        jakarta.servlet.http.HttpServletResponse response = mock(jakarta.servlet.http.HttpServletResponse.class);
        Result<?> result = userController.userList(null, null, null, null, null, null, response);
        assertEquals(200, result.getCode());
        assertEquals(2, ((List<?>) result.getData()).size());
    }

    // 查询用户列表返回空
    @Test
    void testUserListEmpty() {
        when(userService.selectUserList(any(), any(), any(), any(), any(), any())).thenReturn(Collections.emptyList());
        jakarta.servlet.http.HttpServletResponse response = mock(jakarta.servlet.http.HttpServletResponse.class);
        Result<?> result = userController.userList(null, null, null, null, null, null, response);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertTrue(((List<?>) result.getData()).isEmpty());
    }

    // 删除单个用户
    @Test
    void testDeleteUser() {
        when(userService.deleteUserById(anyInt())).thenReturn(1);
        Result<?> result = userController.deleteUser(1);
        assertEquals(200, result.getCode());
    }

    // 批量删除用户
    @Test
    void testDeleteUsers() {
        when(userService.deleteUsersByIds(anyList())).thenReturn(1);
        Map<String, List<Integer>> req = new HashMap<>();
        req.put("ids", Arrays.asList(1, 2, 3));
        Result<?> result = userController.deleteUsers(req);
        assertEquals(200, result.getCode());
    }

    // 批量删除用户传空 ids
    @Test
    void testDeleteUsersEmptyIds() {
        when(userService.deleteUsersByIds(anyList())).thenReturn(1);
        Map<String, List<Integer>> req = new HashMap<>();
        req.put("ids", Collections.emptyList());
        Result<?> result = userController.deleteUsers(req);
        assertEquals(200, result.getCode());
    }

    // 更新用户状态成功
    @Test
    void testUpdateUserStatusSuccess() {
        when(userService.updateUserStatus(anyInt(), anyInt())).thenReturn(1);
        Map<String, Object> req = new HashMap<>();
        req.put("id", 1);
        req.put("status", 1);
        Result<?> result = userController.updateUserStatus(req);
        assertEquals(200, result.getCode());
    }

    // 更新用户状态失败
    @Test
    void testUpdateUserStatusFail() {
        when(userService.updateUserStatus(anyInt(), anyInt())).thenReturn(0);
        Map<String, Object> req = new HashMap<>();
        req.put("id", 1);
        req.put("status", 1);
        Result<?> result = userController.updateUserStatus(req);
        assertEquals(400, result.getCode());
        assertEquals("更新用户状态失败", result.getMessage());
    }

    // 更新用户状态参数缺失
    @Test
    void testUpdateUserStatusParamMissing() {
        Map<String, Object> req = new HashMap<>();
        // 缺少 id 和 status
        Result<?> result = userController.updateUserStatus(req);
        assertEquals(400, result.getCode());
        assertEquals("更新用户状态失败", result.getMessage());
    }

    // 更新用户状态参数类型错误
    @Test
    void testUpdateUserStatusParamTypeError() {
        Map<String, Object> req = new HashMap<>();
        req.put("id", "not_a_number");
        req.put("status", "not_a_number");
        assertThrows(ClassCastException.class, () -> userController.updateUserStatus(req));
    }
} 