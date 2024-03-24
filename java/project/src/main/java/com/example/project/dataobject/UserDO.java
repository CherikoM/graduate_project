package com.example.project.dataobject;

import com.example.project.model.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class UserDO implements Serializable {
    //id
    private Long id;

    //用户名
    private String name;

    //用户密码
    private String password;

    //邮箱
    private String mail;

    //关注
    private String follow;

    //粉丝
    private String fan;

    //个人简介
    private String description;

    //贡献值
    private long contributePoint;

    //头像
    private String avatar;

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

    public User toModel() {
        User user = new User();

        user.setId(getId());
        user.setName(getName());
        user.setPassword(getPassword());
        user.setMail(getMail());


        user.setDescription(getDescription());
        user.setContributePoint(getContributePoint());
        user.setAvatar(getAvatar());
        user.setGmtCreated(getGmtCreated());
        user.setGmtModified(getGmtModified());

        return user;
    }
}
