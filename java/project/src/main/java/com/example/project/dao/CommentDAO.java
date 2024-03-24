package com.example.project.dao;

import com.example.project.dataobject.CommentDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentDAO {

    int addComment(CommentDO commentDO);

    List<CommentDO> getTopCommentByArea(@Param("area")int area, @Param("areaId")Long areaId);

    List<CommentDO> getTopCommentByUser(@Param("userId")Long userId);

    List<CommentDO> getSubComment(@Param("parentId")Long parentId);

    Long getSubCommentCount(@Param("area")int area, @Param("areaId")Long areaId, @Param("parentId")Long parentId);
}
