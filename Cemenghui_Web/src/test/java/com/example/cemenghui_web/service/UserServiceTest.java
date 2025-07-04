package com.example.cemenghui_web.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.*;
import com.example.cemenghui_web.entity.User;
import com.example.cemenghui_web.mapper.UserMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Arrays;

@SpringBootTest
public class UserServiceTest {
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testServiceNotNull() {
        assertNotNull(userService);
    }

    @Test
    void testLoginSuccess() {
        User inputUser = new User();
        inputUser.setUsername("test");
        inputUser.setPassword("123");

        User dbUser = new User();
        dbUser.setUsername("test");
        dbUser.setPassword("123");

        when(userMapper.LoginSearch(inputUser)).thenReturn(dbUser);

        User result = userService.Login(inputUser);
        assertNotNull(result);
        assertEquals("test", result.getUsername());
        verify(userMapper, times(1)).LoginSearch(inputUser);
    }

    @Test
    void testRegister() {
        User user = new User();
        doNothing().when(userMapper).register(user);
        userService.register(user);
        verify(userMapper, times(1)).register(user);
    }

    @Test
    void testFindByUsername() {
        User user = new User();
        when(userMapper.findByUsername("test")).thenReturn(user);
        User result = userService.findByUsername("test");
        assertEquals(user, result);
        verify(userMapper, times(1)).findByUsername("test");
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        doNothing().when(userMapper).updateUser(user);
        userService.updateUser(user);
        verify(userMapper, times(1)).updateUser(user);
    }

    @Test
    void testUpdatePasswordSuccess() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("old");
        when(userMapper.findByUsername("test")).thenReturn(user);
        doNothing().when(userMapper).updatePassword("test", "new");
        boolean result = userService.updatePassword("test", "old", "new");
        assertTrue(result);
        verify(userMapper, times(1)).updatePassword("test", "new");
    }

    @Test
    void testUpdatePassword_UserNotFound() {
        when(userMapper.findByUsername("notfound")).thenReturn(null);
        boolean result = userService.updatePassword("notfound", "old", "new");
        assertFalse(result);
        verify(userMapper, never()).updatePassword(anyString(), anyString());
    }

    @Test
    void testUpdatePassword_WrongOldPassword() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("old");
        when(userMapper.findByUsername("test")).thenReturn(user);
        boolean result = userService.updatePassword("test", "wrong", "new");
        assertFalse(result);
        verify(userMapper, never()).updatePassword(anyString(), anyString());
    }

    @Test
    void testFindPasswordSuccess() {
        User user = new User();
        user.setUsername("test");
        user.setPhone("123456");
        when(userMapper.findByUsername("test")).thenReturn(user);
        doNothing().when(userMapper).updatePassword("test", "new");
        boolean result = userService.findPassword("test", "123456", "new");
        assertTrue(result);
        verify(userMapper, times(1)).updatePassword("test", "new");
    }

    @Test
    void testFindPassword_UserNotFound() {
        when(userMapper.findByUsername("notfound")).thenReturn(null);
        boolean result = userService.findPassword("notfound", "123456", "new");
        assertFalse(result);
        verify(userMapper, never()).updatePassword(anyString(), anyString());
    }

    @Test
    void testFindPassword_WrongPhone() {
        User user = new User();
        user.setUsername("test");
        user.setPhone("654321");
        when(userMapper.findByUsername("test")).thenReturn(user);
        boolean result = userService.findPassword("test", "123456", "new");
        assertFalse(result);
        verify(userMapper, never()).updatePassword(anyString(), anyString());
    }

    @Test
    void testInsertUser() {
        User user = new User();
        when(userMapper.insertUser(user)).thenReturn(1);
        int result = userService.insertUser(user);
        assertEquals(1, result);
        verify(userMapper, times(1)).insertUser(user);
    }

    @Test
    void testSelectUserList() {
        List<User> users = Arrays.asList(new User(), new User());
        when(userMapper.selectUserList("a", "b", "c", 1, 0, 1)).thenReturn(users);
        List<User> result = userService.selectUserList("a", "b", "c", 1, 0, 1);
        assertEquals(2, result.size());
        verify(userMapper, times(1)).selectUserList("a", "b", "c", 1, 0, 1);
    }

    @Test
    void testDeleteUserById() {
        when(userMapper.deleteUserById(1)).thenReturn(1);
        int result = userService.deleteUserById(1);
        assertEquals(1, result);
        verify(userMapper, times(1)).deleteUserById(1);
    }

    @Test
    void testDeleteUsersByIds() {
        List<Integer> ids = Arrays.asList(1, 2);
        when(userMapper.deleteUsersByIds(ids)).thenReturn(2);
        int result = userService.deleteUsersByIds(ids);
        assertEquals(2, result);
        verify(userMapper, times(1)).deleteUsersByIds(ids);
    }

    @Test
    void testUpdateUserStatus() {
        when(userMapper.updateUserStatus(1, 1)).thenReturn(1);
        int result = userService.updateUserStatus(1, 1);
        assertEquals(1, result);
        verify(userMapper, times(1)).updateUserStatus(1, 1);
    }

    @Test
    void testGetUserById() {
        User user = new User();
        when(userMapper.getUserById(1)).thenReturn(user);
        User result = userService.getUserById(1);
        assertEquals(user, result);
        verify(userMapper, times(1)).getUserById(1);
    }
} 