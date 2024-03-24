package com.example.project.dataobject;

import com.alibaba.fastjson2.JSON;
import com.example.project.model.Artist;
import com.example.project.model.MainRelease;
import com.example.project.model.Release;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class ReleaseDO implements Serializable {
    //发行id
    private Long id;

    //发行对应的主id
    private Long mainId;

    //如果有多个发行版本，该发行版是否为基准版
    private int isBase;

    //发行标题
    private String title;

    //发行艺人
    private String artist;

    //发行的封面
    private String picture;

    //发行的厂牌
    private String releaseLabel;

    //发行的格式
    private int format;

    //发行的形式
    private int form;

    //发行国家
    private String country;

    //发行日
    private Date releaseDate;

    //发行的流派
    private String genre;

    //发行的风格
    private String style;

    //发行的曲目表
    private String tracklist;

    //发行的参与人员
    private String credits;

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

    public Release toModel() {
        Release release = new Release();

        release.setId(getId());

        MainRelease mainRelease = new MainRelease();
        mainRelease.setId(getMainId());
        release.setMainRelease(mainRelease);

        release.setTitle(getTitle());

        List<Map> artists = JSON.parseArray(getArtist(), Map.class);
        release.setArtist(artists);

        release.setPicture(getPicture());

        List<Map> labels = JSON.parseArray(getReleaseLabel(), Map.class);
        release.setReleaseLabel(labels);

        String formatStr = null;
        switch (getFormat()) {
            case 1:
                formatStr = "CD";
                break;
            case 2:
                formatStr = "vinyl";
                break;
            case 3:
                formatStr = "cassette";
                break;
            case 4:
                formatStr = "digital";
                break;
            case 5:
                formatStr = "other";
                break;
        }
        if(formatStr != null) {
            release.setFormat(formatStr);
        }

        String formStr = null;
        switch (getForm()) {
            case 1:
                formStr = "LP";
                break;
            case 2:
                formStr = "EP";
                break;
            case 3:
                formStr = "single";
                break;
            case 4:
                formStr = "cooperation";
                break;
            case 5:
                formStr = "other";
                break;
        }
        if(formStr != null) {
            release.setForm(formStr);
        }

        release.setCountry(getCountry());
        release.setReleaseDate(getReleaseDate());

        List<String> genres = JSON.parseArray(getGenre(), String.class);
        release.setGenre(genres);

        List<String> styles = JSON.parseArray(getStyle(), String.class);
        release.setStyle(styles);

        List<Map> tracklistList = JSON.parseArray(getTracklist(), Map.class);
        System.out.println(tracklistList);
//        tracklistList = tracklistList.stream().map(track-> {
//            String artistStr = (String)track.get("artist");
//
//            if(artistStr != null) {
//                List<Map> artistList = JSON.parseArray(artistStr, Map.class);
//                track.put("artist", artistList);
//            }
//
//            String extraArtistStr = (String)track.get("extraartists");
//            if(extraArtistStr != null) {
//                List<Map> extraArtistList = JSON.parseArray(extraArtistStr, Map.class);
//                track.put("extraartists", extraArtistList);
//            }
//
//            return track;
//        }).collect(Collectors.toList());
        release.setTracklist(tracklistList);

        List<Map> creditsList = JSON.parseArray(getCredits(), Map.class);
        release.setCredits(creditsList);

        release.setSupplement(getSupplement());
        release.setGmtCreated(getGmtCreated());
        release.setGmtModified(getGmtModified());

        return release;
    }
}
