package com.example.project.service.impl;

import com.alibaba.fastjson2.JSON;
import com.example.project.common.Paging;
import com.example.project.common.Result;
import com.example.project.dao.CollectionDAO;
import com.example.project.dao.UserDAO;
import com.example.project.dataobject.CollectionDO;
import com.example.project.dataobject.UserDO;
import com.example.project.model.Collection;
import com.example.project.service.CollectionService;
import com.example.project.util.RedisOperationUtils.CollectionUtil;
import com.example.project.util.RedisOperationUtils.FollowUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CollectionServiceImpl implements CollectionService {

    @Autowired
    private CollectionDAO collectionDAO;

    @Autowired
    private CollectionUtil collectionUtil;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private FollowUtil followUtil;

    @Override
    public Long addCollection(CollectionDO collectionDO) {
        collectionDAO.addCollection(collectionDO);
        if(collectionDO.getId() != null) {
            return collectionDO.getId();
        } else {
            return null;
        }
    }

    @Override
    public Paging<Collection> getCollectionByUserId(Long userId, int pageNum, int pageSize) {
        Page<CollectionDO> page = PageHelper.startPage(pageNum, pageSize).doSelectPage(()-> collectionDAO.getCollectionByUserId(userId));
        List<CollectionDO> collectionDOs = page.getResult();
        List<Collection> collections = collectionDOs.stream().map(collectionDO -> {
            Collection collection = collectionDO.toModel();
            collection.setSize(collectionUtil.getColSize(collection.getId()));
            collection.setAddNum(collectionUtil.getColLikerCount(collection.getId()));
            collection.setIsAdd(collectionUtil.isLikeCol(collection.getId(), userId));
            return collection;
        }).collect(Collectors.toList());
        if(page.getResult() == null || page.getResult().size() == 0) {
            return null;
        } else {
            return new Paging<>(page.getPageNum(), page.getPageSize(), page.getPages(), page.getTotal(), collections);
        }
    }

    @Override
    public Result addItemToList(Long colId, String area, Long areaId) {
        boolean isIn = collectionUtil.isInCol(colId, area, areaId);
        if(isIn) {
            return Result.error("Item already in this collection!");
        }
        boolean add = collectionUtil.addItemToList(colId, area, areaId);
        if(add) {
            return Result.success("Add item to list success!");
        } else {
            return Result.error("Add item to list failed!");
        }
    }

    @Override
    public Paging<Map> getCollectionContent(Long colId, int pageNum, int pageSize) {
        long size = collectionUtil.getColSize(colId);
        int totalPage = (int) (size/pageSize);
        if(size%pageSize>0) {
            totalPage++;
        }

        List<Map> result = collectionUtil.getList(colId, pageNum, pageSize);

        if(result == null || result.size() == 0) {
            return null;
        }

        List<Map> r = collectionDAO.getCollectionContent(result);
        return new Paging<>(pageNum, pageSize, totalPage, size, r);
    }

    @Override
    public Collection getCollectionById(Long colId, Long userId) {
        CollectionDO collectionDO = collectionDAO.getCollectionById(colId);
        Collection collection = collectionDO.toModel();
        collection.setSize(collectionUtil.getColSize(collection.getId()));
        collection.setIsAdd(collectionUtil.isLikeCol(colId, userId));
        collection.setAddNum(collectionUtil.getColLikerCount(colId));
        return collection;
    }

    /**
     * 获取一个collection的收藏者
     * @param colId
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public Paging<Map> getColLikerList(Long colId, Long userId, int pageNum, int pageSize) {
        long size = collectionUtil.getColLikerCount(colId);
        int totalPage = (int) (size/pageSize);
        if(size%pageSize>0) {
            totalPage++;
        }

        List<Long> ids = collectionUtil.getColLiker(colId, pageNum, pageSize);

        if(ids == null || ids.size() == 0) {
            return null;
        }

        List<Map> m = userDAO.getFriendList(ids);

        List<Map> result = m.stream().map(map1 -> {
            map1.put("isFollow", followUtil.isFollow(map1.get("id"), userId));
            map1.put("friend", followUtil.isFriend(map1.get("id"), userId));
            return map1;
        }).collect(Collectors.toList());

        return new Paging<>(pageNum, pageSize, totalPage, ids.size(), result);
    }

    /**
     * 获取一个用户收藏的collection
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public Paging<Collection> getUserLikeList(Long userId, int pageNum, int pageSize) {
        long size = collectionUtil.getUserLikeCount(userId);
        int totalPage = (int) (size/pageSize);
        if(size%pageSize>0) {
            totalPage++;
        }

        List<Long> ids = collectionUtil.getUserLike(userId, pageNum, pageSize);

        if(ids == null || ids.size() == 0) {
            return null;
        }

        List<CollectionDO> collectionDOs = collectionDAO.getCollectionsByIds(ids);
        List<Collection> collections = collectionDOs.stream().map(collectionDO -> {
            Collection collection = collectionDO.toModel();
            collection.setSize(collectionUtil.getColSize(collection.getId()));
            collection.setIsAdd(true);
            collection.setAddNum(collectionUtil.getColLikerCount(collection.getId()));
            return collection;
        }).collect(Collectors.toList());

        return new Paging<>(pageNum, pageSize, totalPage, ids.size(), collections);
    }

    @Override
    public boolean likeCollection(Long colId, Long userId) {
        CollectionDO collectionDO = collectionDAO.getCollectionById(colId);
        if(collectionDO.getCreatorId()==userId) {
            return false;
        }
        return collectionUtil.likeCollection(colId, userId);
    }

    @Override
    public boolean dislikeCollection(Long colId, Long userId) {
        CollectionDO collectionDO = collectionDAO.getCollectionById(colId);
        if(collectionDO.getCreatorId()==userId) {
            return false;
        }
        return collectionUtil.disLikeCollection(colId, userId);
    }

    @Override
    public Long deleteCol(Long colId) {
        collectionUtil.deleteCol(colId);
        Long delNum = collectionDAO.deleteCollectionById(colId);
        return delNum;
    }

    @Override
    public void addColContent() {
        List<Long> cols = collectionUtil.getAllCols();
        Map contentMap = cols.stream().collect(Collectors.toMap(col-> col, col-> {
            List<Map> maps = collectionUtil.getList(col);
            return JSON.toJSONString(maps);
        }));

        collectionDAO.updateColContent(contentMap);
    }

    @Override
    public void addColLiker() {
        List<Long> cols = collectionUtil.getAllCols();
        Map likerMap = cols.stream().collect(Collectors.toMap(col-> col, col-> {
            return collectionUtil.getAllLikers(col);
        }));

        collectionDAO.updateColLiker(likerMap);
    }
}
