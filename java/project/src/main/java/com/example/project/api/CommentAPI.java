package com.example.project.api;

import com.example.project.common.Paging;
import com.example.project.common.Result;
import com.example.project.dataobject.CommentDO;
import com.example.project.service.CommentService;
import com.example.project.util.SensitiveWordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/comment")
public class CommentAPI {

    @Autowired
    private CommentService commentService;

    /**
     * 程序启动时进行初始化
     */
    @PostConstruct
    public void init() {
        commentService.sensitiveInit();
    }

    @PutMapping("/add")
    @ResponseBody
    public Result addComment(@RequestBody CommentDO commentDO) {
        int res = commentService.post(commentDO);
        if(res ==1) {
            return Result.success("Add comment success!");
        } else {
            return Result.error("Add comment failed!");
        }
    }

    @GetMapping("/topComment")
    @ResponseBody
    public Result getTopCommentByArea(@RequestParam("area")int area, @RequestParam("areaId") Long areaId, @RequestParam("pageNum")int pageNum, @RequestParam("pageSize")int pageSize) {
        Paging result = commentService.getTopCommentByArea(area, areaId, pageNum, pageSize);
        if(result == null) {
            return Result.error("Get top comment failed!");
        } else {
            return Result.success(result);
        }
    }

    @GetMapping("/userComment")
    @ResponseBody
    public Result getTopCommentByUser(@RequestParam("userId") Long userId, @RequestParam("pageNum")int pageNum, @RequestParam("pageSize")int pageSize) {
        Paging result = commentService.getTopCommentByUser(userId, pageNum, pageSize);
        if(result == null) {
            return Result.error("Get user comment failed!");
        } else {
            return Result.success(result);
        }
    }

    @GetMapping("/subComment")
    @ResponseBody
    public Result getSubComment(@RequestParam("parentId") Long parentId, @RequestParam("pageNum")int pageNum, @RequestParam("pageSize")int pageSize) {
        Paging result = commentService.getSubComment(parentId, pageNum, pageSize);
        if(result == null) {
            return Result.error("Get sub comment failed!");
        } else {
            return Result.success(result);
        }
    }


}
