package com.example.cemenghui_web.service;

import com.example.cemenghui_web.entity.Course;
import com.example.cemenghui_web.mapper.CourseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {
    @Autowired
    private CourseMapper courseMapper;

    public boolean addCourse(Course course) {
        return courseMapper.insertCourse(course) > 0;
    }

    public List<Course> listAll() {
        return courseMapper.listAll();
    }
}