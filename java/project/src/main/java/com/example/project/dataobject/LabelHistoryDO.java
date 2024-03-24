package com.example.project.dataobject;

import com.alibaba.fastjson2.JSON;
import com.example.project.model.Artist;
import com.example.project.model.ArtistHistory;
import com.example.project.model.Label;
import com.example.project.model.LabelHistory;
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
public class LabelHistoryDO implements Serializable {
    //主键
    private Long id;

    //是否为创建条目
    private int isNew;

    //对应的发行id
    private Long labelId;

    //对应的发行标题
    private String labelName;

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

    public LabelHistory toModel() {
        LabelHistory labelHistory = new LabelHistory();
        labelHistory.setId(getId());

        if(getIsNew() == 1) {
            labelHistory.setNew(true);
        } else if(getIsNew() == 0) {
            labelHistory.setNew(false);
        }

        Label label = new Label();
        label.setId(getLabelId());
        labelHistory.setLabel(label);

        labelHistory.setLabelName(getLabelName());

        List<String> changes = JSON.parseArray(getChange(), String.class);
        labelHistory.setChange(changes);

        labelHistory.setUserId(getUserId());
        labelHistory.setUserName(getUserName());
        labelHistory.setInfo(getInfo());
        labelHistory.setReference(getReference());
        labelHistory.setAuditorId(getAuditorId());

        if(getIsPass() == 0) {
            labelHistory.setIsPass("wait");
        } else if(getIsPass() == 1) {
            labelHistory.setIsPass("pass");
        } else {
            labelHistory.setIsPass("no pass");
        }

        labelHistory.setAuditTime(getAuditTime());

        labelHistory.setGmtCreated(getGmtCreated());
        labelHistory.setGmtModified(getGmtModified());

        return labelHistory;
    }
}
