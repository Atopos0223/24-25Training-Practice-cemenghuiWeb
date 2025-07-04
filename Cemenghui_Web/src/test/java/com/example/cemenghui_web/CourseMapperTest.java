package com.example.cemenghui_web;

import com.example.cemenghui_web.entity.Course;
import com.example.cemenghui_web.mapper.CourseMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class CourseMapperTest {

    @Autowired
    private CourseMapper courseMapper;

    private Course testCourse;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
        testCourse = new Course();
        testCourse.setTitle("测试课程");
        testCourse.setAuthor("测试作者");
        testCourse.setIntro("测试介绍");
        testCourse.setStatus("已发布");
        testCourse.setCreateTime(new Date());
    }

    @Test
    void testInsertCourse() {
        // 执行插入
        int result = courseMapper.insertCourse(testCourse);

        // 验证
        assertEquals(1, result, "插入应返回影响行数1");
        assertNotNull(testCourse.getId(), "插入后应生成ID");
    }

    @Test
    @Commit
    void testSelectById() {
        // 准备数据
        courseMapper.insertCourse(testCourse);
        Long insertedId = Long.valueOf(testCourse.getId());

        // 执行查询
        Course found = courseMapper.selectById(insertedId);

        // 验证
        assertNotNull(found, "查询结果不应为null");
        assertEquals(insertedId, found.getId(), "ID应匹配");
        assertEquals("测试课程", found.getTitle(), "标题应匹配");
    }


    @Test
    void testSelectAllCourses() {
        // 准备数据
        courseMapper.insertCourse(testCourse);

        // 执行查询
        List<Course> courses = courseMapper.selectAllCourses();

        // 验证
        assertFalse(courses.isEmpty(), "课程列表不应为空");
        assertTrue(courses.stream().anyMatch(c ->
                c.getTitle().equals("测试课程")), "应包含测试课程");
    }

    @Test
    void testUpdateCourse() {
        // 准备数据
        courseMapper.insertCourse(testCourse);

        // 修改数据
        testCourse.setTitle("修改后的标题");
        testCourse.setIntro("修改后的介绍");

        // 执行更新
        int result = courseMapper.updateCourse(testCourse);

        // 验证
        assertEquals(1, result, "更新应返回影响行数1");

        Course updated = courseMapper.selectById((long) testCourse.getId());
        assertEquals("修改后的标题", updated.getTitle(), "标题应更新");
        assertEquals("修改后的介绍", updated.getIntro(), "介绍应更新");
    }

    @Test
    void testDeleteById() {
        // 准备数据
        courseMapper.insertCourse(testCourse);
        Long idToDelete = Long.valueOf(testCourse.getId());

        // 执行删除
        int result = courseMapper.deleteById(idToDelete);

        // 验证
        assertEquals(1, result, "删除应返回影响行数1");
        assertNull(courseMapper.selectById(idToDelete), "删除后查询应为null");
    }

    @Test
    void testSelectByStatus() {
        // 准备数据
        courseMapper.insertCourse(testCourse);

        // 执行查询
        List<Course> publishedCourses = courseMapper.selectByStatus("已发布");

        // 验证
        assertFalse(publishedCourses.isEmpty(), "应查询到已发布课程");
    }

    @Test
    void testSelectByStatusNot() {
        // 准备数据
        courseMapper.insertCourse(testCourse);

        // 执行查询
        List<Course> notDraftCourses = courseMapper.selectByStatusNot("草稿");

        // 验证
        assertFalse(notDraftCourses.isEmpty(), "应查询到非草稿课程");
    }
}