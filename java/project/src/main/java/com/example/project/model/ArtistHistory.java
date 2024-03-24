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
public class ArtistHistory implements Serializable {

    //主键
    private Long id;

    //是否为创建条目
    private boolean isNew;

    //对应的发行id
    private Artist artist;

    //对应的发行标题
    private String artistName;

    //相比上次版本的更改
    private List<String> change;

    //提交用户的id
    private Long userId;

    //提交用户的用户名
    private String userName;

    //该版本的具体内容
    private String info;

    //用户提交的参考来源
    private String reference;

    //作出判决的审核员id
    private Long auditorId;

    //版本是否审核通过
    private String isPass;

    //版本审核时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Asia/Shanghai")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime auditTime;

    //收到的感谢总数
    private long thanks;

    //是否已感谢
    private boolean isThank;

    //创建时间
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
