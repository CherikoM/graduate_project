package com.example.project.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Comment {

    // id
    private Long id;

    // 条目类型
    private String area;

    // 条目id
    private int areaId;

    // 条目名
    private String areaName;

    // 评论内容
    private String content;

    // 父评论id
    private Long parentId;

    // 子评论
//    private List<Comment> children;

    // 子评论数量
    private Long subCount;

    // 评论人
    private User user;

    // 评论人id
    private Long userId;

    //添加时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Asia/Shanghai")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime gmtCreated;

    //更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Asia/Shanghai")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime gmtModified;
}
