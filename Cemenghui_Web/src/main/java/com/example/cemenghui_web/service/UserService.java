package com.example.cemenghui_web.service;

import com.example.cemenghui_web.entity.User;
import com.example.cemenghui_web.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public User Login(User user){
        System.out.println(userMapper.LoginSearch(user));
        return userMapper.LoginSearch(user);
    }

    public void register(User user) {
        userMapper.register(user);
    }

    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }
    
    public void updateUser(User user) {
        userMapper.updateUser(user);
    }
    
    public boolean updatePassword(String username, String oldPassword, String newPassword) {
        // 先验证旧密码是否正确
        User user = userMapper.findByUsername(username);
        if (user == null || !user.getPassword().equals(oldPassword)) {
            return false;
        }
        
        // 更新密码
        userMapper.updatePassword(username, newPassword);
        return true;
    }
}
