package com.example.project.dataobject;

import com.alibaba.fastjson2.JSON;
import com.example.project.model.Artist;
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

/**
 * 艺人/组合
 */
@Data
public class ArtistDO implements Serializable {
    //艺人/组合id
    private Long id;

    //艺人/组合名
    private String name;

    //艺人/组合简介
    private String profile;

    //性质：0组合；1艺人
    private int type;

    //艺人真名，如果性质是艺人才填写此项
    private String realName;

    //艺人/组合昵称
    private String nickname;

    //艺人/组合所属，如果性质是艺人才填写此项
    private String belong;

    //艺人/组合官网
    private String official;

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

    public Artist toModel() {
        Artist artist = new Artist();

        artist.setId(getId());
        artist.setName(getName());
        artist.setProfile(getProfile());

        if(getType() <= 0) {
            artist.setType("artist");
        } else {
            artist.setType("group");
        }
        artist.setRealName(getRealName());
        artist.setNickname(getNickname());

        List<Map> belongs = JSON.parseArray(getBelong(), Map.class);
        artist.setBelong(belongs);

        List<String> officials = JSON.parseArray(getOfficial(), String.class);
        artist.setOfficial(officials);

        artist.setPicture(getPicture());
        artist.setGmtCreated(getGmtCreated());
        artist.setGmtModified(getGmtModified());

        return artist;
    }
}
