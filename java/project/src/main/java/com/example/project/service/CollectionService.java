package com.example.project.service;

import com.example.project.common.Paging;
import com.example.project.common.Result;
import com.example.project.dataobject.CollectionDO;
import com.example.project.model.Collection;

import java.util.List;
import java.util.Map;

public interface CollectionService {

    Long addCollection(CollectionDO collectionDO);

    Paging<Collection> getCollectionByUserId(Long userId, int pageNum, int pageSize);

    Result addItemToList(Long colId, String area, Long areaId);

    Paging<Map> getCollectionContent(Long colId, int pageNum, int pageSize);

    Collection getCollectionById(Long colId, Long userId);

    Paging<Map> getColLikerList(Long colId, Long userId, int pageNum, int pageSize);

    Paging<Collection> getUserLikeList(Long userId, int pageNum, int pageSize);

    boolean likeCollection(Long colId, Long userId);

    boolean dislikeCollection(Long colId, Long userId);

    Long deleteCol(Long colId);

    void addColContent();

    void addColLiker();
}
