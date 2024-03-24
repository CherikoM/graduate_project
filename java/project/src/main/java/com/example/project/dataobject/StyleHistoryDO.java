package com.example.project.dataobject;

import com.alibaba.fastjson2.JSON;
import com.example.project.model.Release;
import com.example.project.model.ReleaseHistory;
import com.example.project.model.Style;
import com.example.project.model.StyleHistory;
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
public class StyleHistoryDO implements Serializable {

    //历史版本的id
    private Long id;

    //是否为创建条目
    private int isNew;

    //对应的风格id
    private Long styleId;

    //对应的风格英文名
    private String styleEnName;

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

    public StyleHistory toModel() {
        StyleHistory styleHistory = new StyleHistory();
        styleHistory.setId(getId());

        if(getIsNew() == 1) {
            styleHistory.setNew(true);
        } else if(getIsNew() == 0) {
            styleHistory.setNew(false);
        }

        Style style = new Style();

        if(getStyleId() != null) {
            style.setId(getStyleId());
        }
        styleHistory.setStyle(style);

        styleHistory.setStyleEnName(getStyleEnName());

        List<String> changes = JSON.parseArray(getChange(), String.class);
        styleHistory.setChange(changes);

        styleHistory.setUserId(getUserId());
        styleHistory.setUserName(getUserName());
        styleHistory.setInfo(getInfo());
        styleHistory.setReference(getReference());
        styleHistory.setAuditorId(getAuditorId());

        if(getIsPass() == 0) {
            styleHistory.setIsPass("wait");
        } else if(getIsPass() == 1) {
            styleHistory.setIsPass("pass");
        } else {
            styleHistory.setIsPass("no pass");
        }

        styleHistory.setAuditTime(getAuditTime());

        styleHistory.setGmtCreated(getGmtCreated());
        styleHistory.setGmtModified(getGmtModified());

        return styleHistory;
    }
}
