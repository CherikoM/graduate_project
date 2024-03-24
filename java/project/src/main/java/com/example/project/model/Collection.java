package com.example.project.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class Collection implements Serializable {

    //id
    private Long id;

    //乐单名
    private String name;

    //创建者id
    private Long creatorId;

    //创建者名
    private String creatorName;

    //封面
    private String picture;

    //描述
    private String description;

    //内容
    private List<Map> content;

    //内容量
    private long size;

    //对于请求的发起者而言是否已收藏
    private boolean isAdd;

    //收藏量
    private long addNum;

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

    public boolean getIsAdd() {
        return this.isAdd;
    }

    public void setIsAdd(boolean isAdd) {
        this.isAdd = isAdd;
    }
}
