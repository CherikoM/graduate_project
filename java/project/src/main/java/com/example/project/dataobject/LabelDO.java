package com.example.project.dataobject;

import com.alibaba.fastjson2.JSON;
import com.example.project.model.Label;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class LabelDO implements Serializable {
    //厂牌id
    private Long id;

    //厂牌名
    private String name;

    //厂牌图片
    private String picture;

    //父厂牌id
    private Long parentId;

    //父厂牌名
    private String parentName;

    //厂牌简介
    private String profile;

    //厂牌官网
    private String official;

    //厂牌联系方式
    private String contact;

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

    public Label toModel() {
        Label label = new Label();

        label.setId(getId());
        label.setName(getName());
        label.setPicture(getPicture());
        label.setParentId(getParentId());
        label.setParentName(getParentName());
        label.setProfile(getProfile());

        List<String> officials = JSON.parseArray(getOfficial(), String.class);
        label.setOfficial(officials);

        List<String> contacts = JSON.parseArray(getContact(), String.class);
        label.setContact(contacts);

        label.setGmtCreated(getGmtCreated());
        label.setGmtModified(getGmtModified());

        return label;
    }
}
