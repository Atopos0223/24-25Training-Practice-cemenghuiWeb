package com.example.cemenghui_web.mapper;

import com.example.cemenghui_web.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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
    
    /**
     * 更新用户信息
     */
    @Update("UPDATE \"user\" SET \"nickname\" = #{nickname}, \"phone\" = #{phone}, \"email\" = #{email}, \"gender\" = #{gender}, \"company\" = #{company} WHERE \"id\" = #{id}")
    void updateUser(User user);
    
    /**
     * 修改密码
     */
    @Update("UPDATE \"user\" SET \"password\" = #{newPassword} WHERE \"username\" = #{username}")
    void updatePassword(@Param("username") String username, @Param("newPassword") String newPassword);

    /**
     * 根据用户ID查询用户
     */
    @Select("SELECT * FROM \"user\" WHERE \"id\" = #{id}")
    User getUserById(@Param("id") Integer id);
}
