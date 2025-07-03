package com.example.cemenghui_web.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

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
    private String creator_name;


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

    public Date getStart_time() {
        return start_time;
    }

    public void setStart_time(Date start_time) {
        this.start_time = start_time;
    }

    public Date getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Date end_time) {
        this.end_time = end_time;
    }

    public Long getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(Long creator_id) {
        this.creator_id = creator_id;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
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

    public String getCreator_name() {
        return creator_name;
    }

    public void setCreator_name(String creator_name) {
        this.creator_name = creator_name;
    }

    @Override
    public String toString() {
        return "Meeting{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", startTime=" + start_time +
                ", endTime=" + end_time +
                ", creator_id=" + creator_id +
                ", creator_name='" + creator_name + '\'' +
                ", status=" + status +
                ", location='" + location + '\'' +
                ", cover='" + cover + '\'' +
                ", create_time=" + create_time +
                '}';
    }
}