package com.example.cemenghui_web.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

public class ResultTest {
    @Test
    void testAllGetterSetter() {
        Result<String> result = new Result<>();
        LocalDateTime now = LocalDateTime.now();
        result.setCode(200);
        result.setMessage("成功");
        result.setData("data");
        result.setTimestamp(now);
        assertEquals(200, result.getCode());
        assertEquals("成功", result.getMessage());
        assertEquals("data", result.getData());
        assertEquals(now, result.getTimestamp());
    }

    @Test
    void testSuccessAndErrorStaticMethods() {
        Result<String> ok = Result.success("ok");
        assertEquals(200, ok.getCode());
        assertEquals("操作成功", ok.getMessage());
        assertEquals("ok", ok.getData());
        assertNotNull(ok.getTimestamp());

        Result<Void> ok2 = Result.success();
        assertEquals(200, ok2.getCode());
        assertEquals("操作成功", ok2.getMessage());
        assertNull(ok2.getData());
        assertNotNull(ok2.getTimestamp());

        Result<String> err = Result.error(500, "失败");
        assertEquals(500, err.getCode());
        assertEquals("失败", err.getMessage());
        assertNull(err.getData());
        assertNotNull(err.getTimestamp());
    }
} 