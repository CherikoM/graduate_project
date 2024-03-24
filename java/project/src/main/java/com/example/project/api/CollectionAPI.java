package com.example.project.api;

import com.example.project.common.Paging;
import com.example.project.common.Result;
import com.example.project.dataobject.CollectionDO;
import com.example.project.model.Collection;
import com.example.project.service.CollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/collection")
public class CollectionAPI {

    @Autowired
    private CollectionService collectionService;

    @Scheduled(cron = "* */15 * * * ?")
    public void addColContent() {
        collectionService.addColContent();
    }

    @Scheduled(cron = "* */15 * * * ?")
    public void addColLiker() {
        collectionService.addColLiker();
    }

    @PutMapping("/add")
    @ResponseBody
    public Result addCollection(@RequestBody CollectionDO collectionDO) {
        Long result = collectionService.addCollection(collectionDO);
        if(result!=null) {
            return Result.success(result);
        } else {
            return Result.error("Add collection failed!");
        }
    }

    @GetMapping("/all/{userId}")
    @ResponseBody
    public Result getCollectionByUserId(@PathVariable Long userId, @RequestParam("pageNum")int pageNum, @RequestParam("pageSize")int pageSize) {
        Paging result = collectionService.getCollectionByUserId(userId, pageNum, pageSize);
        if(result == null) {
            return Result.error("Get collection by user id failed!");
        } else {
            return Result.success(result);
        }
    }

    @PostMapping("/addItem")
    @ResponseBody
    public Result addItemToList(@RequestParam("colId")Long colId, @RequestParam("area")String area, @RequestParam("areaId")Long areaId) {
        return collectionService.addItemToList(colId, area, areaId);
    }

    @GetMapping("/content")
    @ResponseBody
    public Result getCollectionContent(@RequestParam("colId")Long colId, @RequestParam("pageNum")int pageNum, @RequestParam("pageSize")int pageSize) {
        Paging<Map> result = collectionService.getCollectionContent(colId, pageNum, pageSize);
        if(result == null) {
            return Result.error("Get collection content failed");
        } else {
            return Result.success(result);
        }
    }

    @GetMapping("/id")
    @ResponseBody
    public Result getCollectionById(@RequestParam("id")Long id, @RequestParam("userId") Long userId) {
        Collection result = collectionService.getCollectionById(id, userId);
        if(result == null) {
            return Result.error("Get collection by id failed");
        } else {
            return Result.success(result);
        }
    }

    @GetMapping("/colLiker")
    @ResponseBody
    public Result getColLikerList(@RequestParam("colId")Long colId, @RequestParam("userId") Long userId, @RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) {
        Paging result = collectionService.getColLikerList(colId, userId, pageNum, pageSize);
        if(result == null) {
            return Result.error("Get collection liker list failed");
        } else {
            return Result.success(result);
        }
    }

    @GetMapping("/userLike")
    @ResponseBody
    public Result getUserLikeList(@RequestParam("userId") Long userId, @RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) {
        Paging result = collectionService.getUserLikeList(userId, pageNum, pageSize);
        if(result == null) {
            return Result.error("Get user like list failed");
        } else {
            return Result.success(result);
        }
    }

    @PostMapping("/like")
    @ResponseBody
    public Result likeCollection(@RequestParam("colId") Long colId, @RequestParam("userId") Long userId) {
        boolean result = collectionService.likeCollection(colId, userId);
        if(result) {
            return Result.success("Like collection success!");
        } else {
            return Result.error("Like collection failed!");
        }
    }

    @PostMapping("/dislike")
    @ResponseBody
    public Result dislikeCollection(@RequestParam("colId") Long colId, @RequestParam("userId") Long userId) {
        boolean result = collectionService.dislikeCollection(colId, userId);
        if(result) {
            return Result.success("Dislike collection success!");
        } else {
            return Result.error("Dislike collection failed!");
        }
    }

    @PostMapping("/del/{colId}")
    @ResponseBody
    public Result deleteCol(@PathVariable Long colId) {
        Long result = collectionService.deleteCol(colId);
        if(result>0) {
            return Result.success("Delete collection success!");
        } else {
            return Result.success("Delete collection failed!");
        }
    }

}
