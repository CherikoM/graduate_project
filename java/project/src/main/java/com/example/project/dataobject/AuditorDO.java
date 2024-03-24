package com.example.project.dataobject;

import com.example.project.model.Auditor;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AuditorDO implements Serializable {

    //审核员id
    private Long id;

    //审核员对应的用户id
    private Long userId;

    //审核员的积点
    private Long releasePt;
    private Long artistPt;
    private Long labelPt;
    private Long stylePt;

    //审核员身份是否可用
    private int canUse;

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

    public Auditor toModel() {
        Auditor auditor = new Auditor();
        auditor.setId(getId());
        auditor.setReleasePt(getReleasePt());
        auditor.setArtistPt(getArtistPt());
        auditor.setLabelPt(getLabelPt());
        auditor.setStylePt(getStylePt());

        if(getCanUse() == 1) {
            auditor.setCanUse(true);
        } else {
            auditor.setCanUse(false);
        }

        auditor.setGmtCreated(getGmtCreated());
        auditor.setGmtModified(getGmtModified());

        return auditor;
    }

}
