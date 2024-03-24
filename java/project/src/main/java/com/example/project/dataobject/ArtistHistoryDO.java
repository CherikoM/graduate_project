package com.example.project.dataobject;

import com.alibaba.fastjson2.JSON;
import com.example.project.model.Artist;
import com.example.project.model.ArtistHistory;
import com.example.project.model.Release;
import com.example.project.model.ReleaseHistory;
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
public class ArtistHistoryDO implements Serializable {

    //主键
    private Long id;

    //是否为创建条目
    private int isNew;

    //对应的发行id
    private Long artistId;

    //对应的发行标题
    private String artistName;

    //相比上次版本的更改
    private String change;

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
    private int isPass;

    //版本审核时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Asia/Shanghai")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime auditTime;

    //收到的感谢
    private String thanks;

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

    public ArtistHistory toModel() {
        ArtistHistory artistHistory = new ArtistHistory();
        artistHistory.setId(getId());

        if(getIsNew() == 1) {
            artistHistory.setNew(true);
        } else if(getIsNew() == 0) {
            artistHistory.setNew(false);
        }

        Artist artist = new Artist();
        artist.setId(getArtistId());
        artistHistory.setArtist(artist);

        artistHistory.setArtistName(getArtistName());

        List<String> changes = JSON.parseArray(getChange(), String.class);
        artistHistory.setChange(changes);

        artistHistory.setUserId(getUserId());
        artistHistory.setUserName(getUserName());
        artistHistory.setInfo(getInfo());
        artistHistory.setReference(getReference());
        artistHistory.setAuditorId(getAuditorId());

        if(getIsPass() == 0) {
            artistHistory.setIsPass("wait");
        } else if(getIsPass() == 1) {
            artistHistory.setIsPass("pass");
        } else {
            artistHistory.setIsPass("no pass");
        }

        artistHistory.setAuditTime(getAuditTime());

        artistHistory.setGmtCreated(getGmtCreated());
        artistHistory.setGmtModified(getGmtModified());

        return artistHistory;
    }

}
