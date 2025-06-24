package com.example.cemenghui_web.mapper;

import com.example.cemenghui_web.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE username=#{username} and password=#{password}")
    User LoginSearch(User user);

    @Insert("INSERT INTO user(company, username, password, nickname, phone, email, gender, status, is_super, create_time) " +
            "VALUES(#{company}, #{username}, #{password}, #{nickname}, #{phone}, #{email}, #{gender}, #{status}, #{is_super}, NOW())")
    void register(User user);

    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(String username);
}
