package com.example.cemenghui_web.entity;

import java.sql.Timestamp;

public class News {
    private Integer id;
    private Integer author_id;
    private String title;
    private String image;
    private String summary;
    private String content;
    private Integer status;
    private Timestamp create_time;
    private Timestamp update_time;

    // getter/setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getAuthor_id() { return author_id; }
    public void setAuthor_id(Integer author_id) { this.author_id = author_id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Timestamp getCreate_time() { return create_time; }
    public void setCreate_time(Timestamp create_time) { this.create_time = create_time; }
    public Timestamp getUpdate_time() { return update_time; }
    public void setUpdate_time(Timestamp update_time) { this.update_time = update_time; }
} 