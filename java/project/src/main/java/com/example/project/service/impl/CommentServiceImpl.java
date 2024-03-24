package com.example.project.service.impl;

import com.example.project.common.Paging;
import com.example.project.dao.CommentDAO;
import com.example.project.dao.SensitiveWordDAO;
import com.example.project.dataobject.CommentDO;
import com.example.project.model.Comment;
import com.example.project.service.CommentService;
import com.example.project.util.SensitiveWordUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentDAO commentDAO;

    @Autowired
    private SensitiveWordDAO sensitiveWordDAO;

    @Override
    public void sensitiveInit() {
        Set<String> words = sensitiveWordDAO.getWords();
        SensitiveWordUtil.init(words);
    }

    @Override
    public int post(CommentDO commentDO) {
        String content = commentDO.getContent();
        // 将敏感词用星号*替换
        content = SensitiveWordUtil.replaceSensitiveWord(content, "*");
        commentDO.setContent(StringEscapeUtils.escapeHtml4(content));
        return commentDAO.addComment(commentDO);
    }

    @Override
    public Paging<Comment> getTopCommentByArea(int area, Long areaId, int pageNum, int pageSize) {
        Page<CommentDO> page = PageHelper.startPage(pageNum, pageSize).doSelectPage(()-> commentDAO.getTopCommentByArea(area, areaId));
        List<CommentDO> commentDOs = page.getResult();
        if(commentDOs == null || commentDOs.size() == 0) {
            return null;
        }
        List<Comment> comments = commentDOs.stream().map(commentDO -> {
            Comment comment = commentDO.toModel();
            comment.setSubCount(commentDAO.getSubCommentCount(area, areaId, comment.getId()));
            return comment;
        }).collect(Collectors.toList());

        return new Paging<>(page.getPageNum(), page.getPageSize(), page.getPages(), page.getTotal(), comments);
    }

    @Override
    public Paging<Comment> getTopCommentByUser(Long userId, int pageNum, int pageSize) {
        Page<CommentDO> page = PageHelper.startPage(pageNum, pageSize).doSelectPage(()-> commentDAO.getTopCommentByUser(userId));
        List<CommentDO> commentDOs = page.getResult();
        if(commentDOs == null || commentDOs.size() == 0) {
            return null;
        }
        List<Comment> comments = commentDOs.stream().map(CommentDO::toModel).collect(Collectors.toList());

        return new Paging<>(page.getPageNum(), page.getPageSize(), page.getPages(), page.getTotal(), comments);
    }

    @Override
    public Paging<Comment> getSubComment(Long parentId, int pageNum, int pageSize) {
        Page<CommentDO> page = PageHelper.startPage(pageNum, pageSize).doSelectPage(()-> commentDAO.getSubComment(parentId));
        List<CommentDO> commentDOs = page.getResult();
        if(commentDOs == null || commentDOs.size() == 0) {
            return null;
        }
        List<Comment> comments = commentDOs.stream().map(CommentDO::toModel).collect(Collectors.toList());
        return new Paging<>(page.getPageNum(), page.getPageSize(), page.getPages(), page.getTotal(), comments);
    }

}
