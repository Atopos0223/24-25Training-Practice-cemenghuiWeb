package com.example.cemenghui_web.controller;

import com.example.cemenghui_web.entity.Result;
import com.example.cemenghui_web.entity.User;
import com.example.cemenghui_web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
public class UserController {
    @Autowired
    private UserService userService;
    @PostMapping ("/login")
    public Result<User> login(@RequestBody User user) {

        User loginUser = userService.Login(user);
        if (loginUser != null) {
            return Result.success(loginUser);
        } else {
            return Result.error(404,"登录失败");
        }
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody User user) {
        if (userService.findByUsername(user.getUsername()) != null) {
            return Result.error(400, "用户名已存在");
        }
        userService.register(user);
        return Result.success();
    }
    
    @PostMapping("/updateUser")
    public Result<?> updateUser(@RequestBody User user) {
        try {
            userService.updateUser(user);
            return Result.success();
        } catch (Exception e) {
            return Result.error(500, "更新用户信息失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/updatePassword")
    public Result<?> updatePassword(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String oldPassword = request.get("oldPassword");
            String newPassword = request.get("newPassword");
            
            boolean success = userService.updatePassword(username, oldPassword, newPassword);
            if (success) {
                return Result.success();
            } else {
                return Result.error(400, "旧密码错误或用户不存在");
            }
        } catch (Exception e) {
            return Result.error(500, "修改密码失败: " + e.getMessage());
        }
    }
}
