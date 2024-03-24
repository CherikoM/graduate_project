package com.example.project.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class User implements Serializable {

    //id
    private Long id;

    //用户名
    private String name;

    //用户密码
    private String password;

    //邮箱
    private String mail;

    //关注
    private long follow;

    //粉丝
    private long fan;

    //是否关注（当获取该数据的userId并非此数据的userId时）
    private boolean isFollow;

    //是否被关注（当获取该数据的userId并非此数据的userId时）
    private boolean isFollower;

    //是否互粉（当获取该数据的userId并非此数据的userId时）
    private boolean isFriend;

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

    public boolean getIsFollow() {
        return isFollow;
    }

    public void setIsFollow(boolean f) {
        this.isFollow = f;
    }

    public boolean getIsFollower() {
        return isFollower;
    }

    public void setIsFollower(boolean f) {
        this.isFollower = f;
    }

    public boolean getIsFriend() {
        return isFriend;
    }

    public void setIsFriend(boolean f) {
        this.isFriend = f;
    }
}
