package com.example.cemenghui_web.entity;


import java.time.LocalDateTime;

public class News {
    private Long id;              // 新闻ID
    private Long author_id;        // 作者ID（关联用户表）
    private String title;         // 标题
    private String image;         // 图片路径
    private String summary;       // 简介
    private String content;       // 内容
    private Integer status;       // 状态（0-草稿，1-待审，2-已发布）
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(Long author_id) {
        this.author_id = author_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "News{" +
                "id=" + id +
                ", authorId=" + author_id +
                ", title='" + title + '\'' +
                ", image='" + image + '\'' +
                ", summary='" + summary + '\'' +
                ", content='" + content + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}

