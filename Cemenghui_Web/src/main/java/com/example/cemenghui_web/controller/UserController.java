package com.example.cemenghui_web.controller;

import com.example.cemenghui_web.entity.Result;
import com.example.cemenghui_web.entity.User;
import com.example.cemenghui_web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
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

    @PostMapping("/findPassword")
    public Result<?> findPassword(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String phone = request.get("phone");
            String newPassword = request.get("newPassword");
            boolean success = userService.findPassword(username, phone, newPassword);
            if (success) {
                return Result.success();
            } else {
                return Result.error(400, "用户名或手机号不正确");
            }
        } catch (Exception e) {
            return Result.error(500, "找回密码失败: " + e.getMessage());
        }
    }

    // 新增用户（管理员用）
    @PostMapping("/addUser")
    public Result<?> addUser(@RequestBody User user) {
        System.out.println("收到新增用户请求：" + user);
        userService.insertUser(user);
        return Result.success();
    }

    // 查询用户列表（可加条件、分页参数）
    @GetMapping("/userList")
    public Result<?> userList(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer is_super,
            @RequestParam(required = false) Integer gender,
            HttpServletResponse response
    ) {
        response.setHeader("Cache-Control", "no-cache");
        return Result.success(userService.selectUserList(username, phone, company, status, is_super, gender));
    }

    // 删除单个用户
    @DeleteMapping("/deleteUser/{id}")
    public Result<?> deleteUser(@PathVariable Integer id) {
        userService.deleteUserById(id);
        return Result.success();
    }

    // 批量删除用户
    @PostMapping("/deleteUsers")
    public Result<?> deleteUsers(@RequestBody Map<String, List<Integer>> request) {
        List<Integer> ids = request.get("ids");
        userService.deleteUsersByIds(ids);
        return Result.success();
    }

    // 更新用户状态
    @PostMapping("/updateUserStatus")
    public Result<?> updateUserStatus(@RequestBody Map<String, Object> requestBody) {
        // 手动从Map中提取参数并转换类型
        Integer id = (Integer) requestBody.get("id");
        Integer status = (Integer) requestBody.get("status");

        // 调用服务层
        int result = userService.updateUserStatus(id, status);
        if (result > 0) {
            return Result.success();
        } else {
            return Result.error(400, "更新用户状态失败");
        }
    }
}
