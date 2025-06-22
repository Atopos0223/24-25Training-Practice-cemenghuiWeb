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

}
