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
public class Artist implements Serializable {
    //艺人/组合id
    private Long id;

    //艺人/组合名
    private String name;

    //艺人/组合简介
    private String profile;

    //性质
    private String type;

    //艺人真名，如果性质是艺人才填写此项
    private String realName;

    //艺人/组合昵称
    private String nickname;

    //艺人/组合所属，如果性质是艺人才填写此项
    private List<Map> belong;

    //成员是否活跃于该组合，仅当性质是组合才填写此项
    private boolean isActive;

    //艺人/组合官网
    private List<String> official;

    //艺人/组合图片
    private String picture;

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