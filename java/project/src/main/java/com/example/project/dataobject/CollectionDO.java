package com.example.project.dataobject;

import com.alibaba.fastjson2.JSON;
import com.example.project.model.Collection;
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

@Data
public class CollectionDO implements Serializable {

    //id
    private Long id;

    //乐单名
    private String name;

    //创建者id
    private Long creatorId;

    //创建者名
    private String creatorName;

    //封面
    private String picture;

    //描述
    private String description;

    //内容
    private String content;

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

    public Collection toModel() {
        Collection collection = new Collection();
        collection.setId(getId());
        collection.setName(getName());
        collection.setCreatorId(getCreatorId());
        collection.setCreatorName(getCreatorName());
        collection.setPicture(getPicture());
        collection.setDescription(getDescription());

        if(getContent() != null) {
            List<Map> contentList = JSON.parseArray(getContent(), Map.class);
            collection.setContent(contentList);
            collection.setSize(contentList.size());
        }


        collection.setGmtCreated(getGmtCreated());
        collection.setGmtModified(getGmtModified());

        return collection;
    }

}
