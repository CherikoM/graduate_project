package com.example.project.dao;

import com.example.project.dataobject.CollectionDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CollectionDAO {

    int addCollection(CollectionDO collectionDO);

    List<CollectionDO> getCollectionByUserId(@Param("userId") Long userId);

    List<Map> getCollectionContent(@Param("colList")List<Map> colList);

    CollectionDO getCollectionById(@Param("colId")Long colId);

    List<CollectionDO> getCollectionsByIds(@Param("ids")List<Long> ids);

    long deleteCollectionById(@Param("id") Long id);

    int updateColContent(@Param("contentMap")Map contentMap);

    int updateColLiker(@Param("likerMap")Map likerMap);

}
