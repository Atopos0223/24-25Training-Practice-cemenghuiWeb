package com.example.cemenghui_web.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Meeting {
    private Long id;
    private String title;
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private java.util.Date start_time;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private java.util.Date end_time;
    private Long creator_id;
    private Integer status;
    private String location;
    private String cover;
    private java.util.Date create_time;


    public Meeting() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public java.util.Date getStartTime() {
        return start_time;
    }

    public void setStartTime(java.util.Date startTime) {
        this.start_time = startTime;
    }

    public java.util.Date getEndTime() {
        return end_time;
    }

    public void setEndTime(java.util.Date endTime) {
        this.end_time = endTime;
    }

    public Long getCreatorId() {
        return creator_id;
    }

    public void setCreatorId(Long creatorId) {
        this.creator_id = creatorId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public java.util.Date getCreateTime() {
        return create_time;
    }

    public void setCreateTime(java.util.Date createTime) {
        this.create_time = createTime;
    }


} 