package com.example.cemenghui_web.mapper;

import com.example.cemenghui_web.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE username=#{username} and password=#{password}")
    User LoginSearch(User user);
}
