package com.example.cemenghui_web.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Result<T> implements Serializable {
        private int code;
        private String message;
        private T data;
        private LocalDateTime timestamp;

        public Result() {
            this.timestamp = LocalDateTime.now();
        }

        public static <T> Result<T> success(T data) {
            Result<T> result = new Result<>();
            result.setCode(200);
            result.setMessage("操作成功");
            result.setData(data);
            return result;
        }

        public static <T> Result<T> success() {
            return success(null);
        }

        public static <T> Result<T> error(int code, String message) {
            Result<T> result = new Result<>();
            result.setCode(code);
            result.setMessage(message);
            return result;
        }

        // Getters and Setters
        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }
}

