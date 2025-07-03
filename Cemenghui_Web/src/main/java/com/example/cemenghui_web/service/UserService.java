package com.example.cemenghui_web.service;

import com.example.cemenghui_web.entity.User;
import com.example.cemenghui_web.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

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

    /**
     * 找回密码：根据用户名和手机号校验，正确则重置密码
     */
    public boolean findPassword(String username, String phone, String newPassword) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return false;
        }
        if (!user.getPhone().equals(phone)) {
            return false;
        }
        userMapper.updatePassword(username, newPassword);
        return true;
    }


    // 新增用户
    public int insertUser(User user) {
        return userMapper.insertUser(user);
    }

    // 查询用户列表（可按条件查询）
    public List<User> selectUserList(String username, String phone, String company, Integer status, Integer is_super, Integer gender) {
        return userMapper.selectUserList(username, phone, company, status, is_super, gender);
    }

    // 删除单个用户
    public int deleteUserById(Integer id) {
        return userMapper.deleteUserById(id);
    }

    // 批量删除用户
    public int deleteUsersByIds(List<Integer> ids) {
        return userMapper.deleteUsersByIds(ids);
    }

    // 更新用户状态（启用/禁用）
    public int updateUserStatus(Integer id, Integer status) {
        return userMapper.updateUserStatus(id, status);
    }
    public User getUserById(Integer id) {
        return userMapper.getUserById(id);
    }
}
