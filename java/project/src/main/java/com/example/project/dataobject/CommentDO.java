package com.example.project.dataobject;

import com.example.project.model.Comment;
import com.example.project.model.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDO {

    // id
    private Long id;

    // 条目类型
    private int area;

    // 条目id
    private int areaId;

    // 条目名
    private String areaName;

    // 评论内容
    private String content;

    // 父评论id
    private Long parentId;

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

    public Comment toModel() {
        Comment comment = new Comment();
        comment.setId(getId());

        String area = null;
        switch (getArea()) {
            case 1:
                area = "release";
                break;
            case 2:
                area = "artist";
                break;
            case 3:
                area = "label";
                break;
            case 4:
                area = "style";
                break;
        }
        comment.setArea(area);

        comment.setAreaId(getAreaId());
        comment.setAreaName(getAreaName());
        comment.setContent(getContent());
        comment.setParentId(getParentId());

//        User user = new User();
//        user.setId(getUserId());
        comment.setUser(getUser());
        if(getUser()==null) {
            comment.setUserId(getUserId());
        }

        comment.setGmtCreated(getGmtCreated());
        comment.setGmtModified(getGmtModified());

        return comment;
    }
}
