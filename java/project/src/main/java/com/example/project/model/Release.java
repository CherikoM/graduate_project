package com.example.project.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class Release implements Serializable {
    //发行id
    private Long id;

    private MainRelease mainRelease;

    //发行的其他版本
    private List<Map> otherVersion;

    //发行标题
    private String title;

    //发行艺人
    private List<Map> artist;

    //发行的封面
    private String picture;

    //发行的厂牌
    private List<Map> releaseLabel;

    //发行的格式
    private String format;

    //发行的形式
    private String form;

    //发行国家
    private String country;

    //发行日
    private Date releaseDate;

    //发行的流派
    private List<String> genre;

    //发行的风格
    private List<String> style;

    //发行的曲目表
    private List<Map> tracklist;

    //发行的参与人员
    private List<Map> credits;

    //发行的补充信息
    private String supplement;

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
