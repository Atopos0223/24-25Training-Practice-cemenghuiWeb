package com.example.cemenghui_web.mapper;

import com.example.cemenghui_web.entity.News;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NewsMapper {
    @Insert("INSERT INTO news (" +
            "author_id, title, image, summary, content, " +
            "status, create_time, update_time) " +
            "VALUES (" +
            "#{author_id}, #{title}, #{image}, #{summary}, #{content}, " +
            "#{status}, #{createTime}, #{updateTime})")
    boolean addNews(News news);

}
