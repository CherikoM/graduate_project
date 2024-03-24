package com.example.project.dataobject;

import com.alibaba.fastjson2.JSON;
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
public class ReleaseHistoryDO implements Serializable {

    //历史版本的id
    private Long id;

    //是否为创建条目
    private int isNew;

    //对应的发行id
    private Long releaseId;

    //对应的发行标题
    private String title;

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

    public ReleaseHistory toModel() {
        ReleaseHistory releaseHistory = new ReleaseHistory();
        releaseHistory.setId(getId());

        if(getIsNew() == 1) {
            releaseHistory.setNew(true);
        } else if(getIsNew() == 0) {
            releaseHistory.setNew(false);
        }

        Release release = new Release();
        release.setId(getReleaseId());
        releaseHistory.setRelease(release);

        releaseHistory.setTitle(getTitle());

        List<String> changes = JSON.parseArray(getChange(), String.class);
        releaseHistory.setChange(changes);

        releaseHistory.setUserId(getUserId());
        releaseHistory.setUserName(getUserName());
        releaseHistory.setInfo(getInfo());
        releaseHistory.setReference(getReference());
        releaseHistory.setAuditorId(getAuditorId());

        if(getIsPass() == 0) {
            releaseHistory.setIsPass("wait");
        } else if(getIsPass() == 1) {
            releaseHistory.setIsPass("pass");
        } else {
            releaseHistory.setIsPass("no pass");
        }

        releaseHistory.setAuditTime(getAuditTime());

        releaseHistory.setGmtCreated(getGmtCreated());
        releaseHistory.setGmtModified(getGmtModified());

        return releaseHistory;
    }
}
