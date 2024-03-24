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

@Data
public class Style implements Serializable {
    public static String GENRE = "genre";
    public static String PRIMARY_STYLE = "primary";
    public static String SUBDIVIDE_STYLE = "subdivide";

    //流派/风格id
    private long id;
    //流派/风格中文名，可以为空
    private String name;
    //流派/风格英文名，必须非空
    private String enName;
    //如果流派/风格的中文名非空，值为中文名，否则为英文名
    private String value;
    //流派/风格别名
    private List<String> otherName;
    //流派/风格级别
    private String type;
    //流派/风格介绍，富文本
    private String description;
    //流派/风格所属
    private List<String> belong;
    //流派/风格下属分支
    private List<Style> branch;
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
