package com.example.cemenghui_web.mapper;

import com.example.cemenghui_web.entity.News;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface NewsMapper {
    @Insert("INSERT INTO \"news\" (\"author_id\", \"title\", \"image\", \"summary\", \"content\", \"status\", \"create_time\", \"update_time\") " +
            "VALUES (#{author_id}, #{title}, #{image}, #{summary}, #{content}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertNews(News news);

    @Select("SELECT * FROM \"news\" ORDER BY \"create_time\" DESC")
    List<News> getAllNews();

    @Select("SELECT * FROM \"news\" WHERE \"id\" = #{id}")
    News getNewsById(@Param("id") Integer id);

    @Delete("DELETE FROM \"news\" WHERE \"id\" = #{id}")
    int deleteNewsById(@Param("id") Long id);

    @Update("UPDATE \"news\" SET \"title\"=#{title}, \"image\"=#{image}, \"summary\"=#{summary}, \"content\"=#{content}, \"status\"=#{status}, \"update_time\"=NOW() WHERE \"id\"=#{id}")
    int updateNews(News news);

    @Update("UPDATE \"news\" SET \"status\"=#{status}, \"update_time\"=NOW() WHERE \"id\"=#{id}")
    int auditNews(@Param("id") Integer id, @Param("status") Integer status);
} 