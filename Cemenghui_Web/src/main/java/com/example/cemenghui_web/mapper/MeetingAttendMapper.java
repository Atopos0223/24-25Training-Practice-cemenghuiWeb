package com.example.cemenghui_web.mapper;

import com.example.cemenghui_web.entity.MeetingAttend;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MeetingAttendMapper {
    @Insert("INSERT INTO meeting_attend (unit, name, gender, phone, email, arrival_time) VALUES (#{unit}, #{name}, #{gender}, #{phone}, #{email}, #{arrivalTime})")
    void insertMeetingAttend(MeetingAttend meetingAttend);

    @Select("SELECT * FROM meeting_attend")
    java.util.List<MeetingAttend> selectAllMeetingAttend();
} 