package com.example.project.dataobject;

import com.alibaba.fastjson2.JSON;
import com.example.project.model.Style;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 流派/风格
 */
@Data
public class StyleDO implements Serializable {
    //流派/风格id
    private long id;

    //流派/风格名
    private String name;

    //流派/风格英文名
    private String enName;

    //流派/风格别名
    private String otherName;

    //类型，0流派1大风格2细分风格
    private int type;

    //流派/风格介绍，富文本
    private String description;

    //上属流派/风格
    private String belong;

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

    public Style toModel() {
        Style style = new Style();

        style.setId(getId());
        style.setName(getName());
        style.setEnName(getEnName());

        style.setValue(getName());
        if(getName() == null) style.setValue(getEnName());

        List<String> otherNames = JSON.parseArray(getOtherName(), String.class);
        style.setOtherName(otherNames);

        if(getType() == 0) {
            style.setType(Style.GENRE);
        } else if (getType() == 1) {
            style.setType(Style.PRIMARY_STYLE);
        } else if (getType() == 2) {
            style.setType(Style.SUBDIVIDE_STYLE);
        }

        style.setDescription(getDescription());

        List<String> belongs = JSON.parseArray(getBelong(), String.class);
        style.setBelong(belongs);

        style.setGmtCreated(getGmtCreated());
        style.setGmtModified(getGmtModified());

        return style;
    }
}
