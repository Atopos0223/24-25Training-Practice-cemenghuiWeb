package com.example.cemenghui_web.mapper;

import com.example.cemenghui_web.entity.Meeting;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Result;
import java.util.List;

@Mapper
public interface MeetingMapper {
    @Insert("INSERT INTO meeting (title, content, start_time, end_time, location, cover, creator_id, create_time, status) VALUES (#{title}, #{content}, #{startTime}, #{endTime}, #{location}, #{cover}, #{creatorId}, #{createTime}, #{status})")
    void insertMeeting(Meeting meeting);

    @Select("SELECT * FROM meeting")
    List<Meeting> selectAllMeetings();

    @Delete("DELETE FROM meeting WHERE id = #{id}")
    void deleteMeeting(Long id);

    @Select("SELECT * FROM meeting WHERE id = #{id}")
    Meeting selectMeetingById(Long id);

    @Select("SELECT * FROM meeting WHERE id = #{id}")
    Meeting selectMeetingWithCreatorNameById(Long id);

    @Update("UPDATE meeting SET title=#{title}, content=#{content}, start_time=#{startTime}, end_time=#{endTime}, location=#{location}, cover=#{cover} WHERE id=#{id}")
    void updateMeeting(Meeting meeting);


} 