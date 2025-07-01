package com.example.cemenghui_web.mapper;

import com.example.cemenghui_web.entity.News;
import com.example.cemenghui_web.entity.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

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

    // 查询用户列表（可按条件查询）
    @Select({
        "<script>",
        "SELECT * FROM \"user\" WHERE 1=1",
        "<if test='username != null and username != \"\"'> AND \"username\" = #{username}</if>",
        "<if test='phone != null and phone != \"\"'> AND \"phone\" = #{phone}</if>",
        "<if test='company != null and company != \"\"'> AND \"company\" = #{company}</if>",
        "<if test='status != null'> AND \"status\" = #{status}</if>",
        "<if test='is_super != null'> AND \"is_super\" = #{is_super}</if>",
        "<if test='gender != null'> AND \"gender\" = #{gender}</if>",
        "</script>"
    })
    List<User> selectUserList(@Param("username") String username,
                             @Param("phone") String phone,
                             @Param("company") String company,
                             @Param("status") Integer status,
                             @Param("is_super") Integer is_super,
                             @Param("gender") Integer gender);

    // 新增用户
    @Insert("INSERT INTO \"user\"(\"company\", \"username\", \"password\", \"nickname\", \"phone\", \"email\", \"gender\", \"status\", \"is_super\", \"create_time\") " +
            "VALUES(#{company}, #{username}, #{password}, #{nickname}, #{phone}, #{email}, #{gender}, #{status}, #{is_super}, NOW())")
    int insertUser(User user);

    // 删除单个用户
    @Delete("DELETE FROM \"user\" WHERE \"id\" = #{id}")
    int deleteUserById(@Param("id") Integer id);

    // 批量删除用户
    @Delete({
        "<script>",
        "DELETE FROM \"user\" WHERE \"id\" IN",
        "<foreach collection='ids' item='id' open='(' separator=',' close=')'>",
        "#{id}",
        "</foreach>",
        "</script>"
    })
    int deleteUsersByIds(@Param("ids") List<Integer> ids);

    // 更新用户状态（启用/禁用）
    @Update("UPDATE \"user\" SET \"status\" = #{status} WHERE \"id\" = #{id}")
    int updateUserStatus(@Param("id") Integer id, @Param("status") Integer status);

}
