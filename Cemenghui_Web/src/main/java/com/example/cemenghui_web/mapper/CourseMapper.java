package com.example.cemenghui_web.mapper;

import com.example.cemenghui_web.entity.Course;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CourseMapper {

    // 使用双引号包裹字段名（与UserMapper风格一致）
    @Insert("INSERT INTO \"course\" (\"title\", \"author\", \"coverUrl\", \"intro\", \"videoUrl\", \"status\", \"createTime\") " +
            "VALUES (#{title}, #{author}, #{coverUrl}, #{intro}, #{videoUrl}, #{status}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertCourse(Course course);

    @Select("SELECT * FROM course WHERE id = #{id}")
    Course selectById(@Param("id") Long id);

    @Select("SELECT * FROM \"course\" ORDER BY \"createTime\" DESC")
    List<Course> selectAllCourses();

    List<Course> listAll();

    @Update("UPDATE \"course\" SET \"title\"=#{title}, \"author\"=#{author}, \"coverUrl\"=#{coverUrl}, " +
            "\"intro\"=#{intro}, \"videoUrl\"=#{videoUrl}, \"status\"=#{status} WHERE \"id\"=#{id}")
    int updateCourse(Course existingCourse);

    @Delete("DELETE FROM \"course\" WHERE \"id\"=#{id}")
    int deleteById(Long id);
}