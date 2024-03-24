package com.example.project.service;

import com.example.project.common.Paging;
import com.example.project.dataobject.CommentDO;
import com.example.project.model.Comment;

import java.util.List;

public interface CommentService {

    void sensitiveInit();

    int post(CommentDO commentDO);

    Paging<Comment> getTopCommentByArea(int area, Long areaId, int pageNum, int pageSize);

    Paging<Comment> getTopCommentByUser(Long userId, int pageNum, int pageSize);

    Paging<Comment> getSubComment(Long parentId, int pageNum, int pageSize);
}
