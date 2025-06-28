package com.example.cemenghui_web.mapper;

import com.example.cemenghui_web.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM \"user\" WHERE \"username\" = #{username} AND \"password\" = #{password}")
    User LoginSearch(User user);

    /**
     * 注册新用户
     */
    @Insert("INSERT INTO \"user\"(\"company\", \"username\", \"password\", \"nickname\", \"phone\", \"email\", \"gender\", \"status\", \"is_super\", \"create_time\") " +
            "VALUES(#{company}, #{username}, #{password}, #{nickname}, #{phone}, #{email}, #{gender}, #{status}, #{is_super}, NOW())")
    void register(User user);

    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM \"user\" WHERE \"username\" = #{username}")
    User findByUsername(@Param("username") String username);
}
