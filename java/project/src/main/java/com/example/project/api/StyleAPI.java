package com.example.project.api;

import com.example.project.common.Result;
import com.example.project.dataobject.ArtistHistoryDO;
import com.example.project.dataobject.StyleHistoryDO;
import com.example.project.model.ArtistHistory;
import com.example.project.model.Label;
import com.example.project.model.Style;
import com.example.project.model.StyleHistory;
import com.example.project.service.StyleService;
import com.example.project.util.KeywordUtil;
import com.example.project.util.RedisOperationUtils.LockUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * 与style相关的操作的api
 */
@Controller
@RequestMapping("/style")
public class StyleAPI {
    @Autowired
    private StyleService styleService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private LockUtil lockUtil;

    /**
     * 程序启动时进行初始化
     */
    @PostConstruct
    public void init() {
        styleService.getNotAuditStyleVersion();
    }

    /**
     * 启动后每隔五分钟进行一次感谢缓存的持久化
     */
    @Scheduled(cron = "* */5 *  * * ? ")
    public void styleContributeThanksCount() {
        System.out.println("我开始持久化点赞数据啦");
        styleService.styleContributeThanksCount();
    }

    /**
     * 获取所有genre
     * @return
     */
    @GetMapping("/allGenre")
    @ResponseBody
    public Result getAllGenre() {
        List result = styleService.getAllGenre();
        if(result!=null&&result.size()>0) {
            return Result.success(result);
        } else {
            return Result.error("Get all genre failed!");
        }
    }

    /**
     * 获取所有style
     * @return
     */
    @GetMapping("/allStyle")
    @ResponseBody
    public Result getAllStyle() {
        List result = styleService.getAllStyle();
        if(result!=null&&result.size()>0) {
            return Result.success(result);
        } else {
            return Result.error("Get all genre failed!");
        }
    }

    /**
     * 获取所有genre和primary style
     * @return
     */
    @GetMapping("/genrePrimary")
    @ResponseBody
    public Result getGenreAndPrimary() {
        List result = styleService.getGenreAndPrimary();
        if(result!=null&&result.size()>0) {
            return Result.success(result);
        } else {
            return Result.error("Get genre and primary failed!");
        }
    }

    /**
     * 获取所有genre和style并分组
     * @return
     */
    @GetMapping("/styleGroup")
    @ResponseBody
    public Result<List<Style>> getStyleGroup() {
        List<Style> result = styleService.styleGrouper();
        if (result.size() > 0) {
            return Result.success(result);
        } else {
            return Result.error("出错啦！");
        }
    }

    /**
     * 通过enName获取style
     * @param enName
     * @return
     */
    @GetMapping("/name/{enName}")
    @ResponseBody
    public Result getStyleByEnName(@PathVariable("enName") String enName) {
        Style result = styleService.getStyleByEnName(enName);
        if(result!=null) {
            return Result.success(result);
        } else {
            return Result.error("Have repetition style En name!");
        }
    }

    /**
     * enName重复检测
     * @param enName
     * @return
     */
    @GetMapping("/canUse/{enName}")
    @ResponseBody
    public Result canUseEnName(@PathVariable("enName")String enName) {
        Style result = styleService.getStyleByEnName(enName);
        if(result==null) {
            return Result.success("Style name can use");
        } else {
            return Result.error("Style name already exist!");
        }
    }

    /**
     * 添加styleHistory
     * @param styleHistoryDO
     * @return
     */
    @PutMapping("/version/add")
    @ResponseBody
    public Result addStyleVersion(@RequestBody StyleHistoryDO styleHistoryDO) {
        if(styleHistoryDO.getIsNew()!=1) {
            boolean isLock = lockUtil.isLock(styleHistoryDO.getStyleEnName(), LockUtil.MODIFYING_STYLE);
            if (isLock) {
                return Result.error("Add style version failed!");
            }
        }
        int addNum = styleService.addStyleVersion(styleHistoryDO);
        if(addNum <= 0) {
            return Result.error("Add style version failed!");
        } else {
            if(styleHistoryDO.getIsNew()!=1) {
                lockUtil.lock(styleHistoryDO.getStyleEnName(), styleHistoryDO.getUserId(), LockUtil.MODIFYING_STYLE);
            }
            return Result.success("Add style version succeed!");
        }
    }

    /**
     * 获取一个style审核任务
     * @return
     */
    @GetMapping("/mission")
    @ResponseBody
    public Result dispatchMission() {
        StyleHistory styleHistory = styleService.dispatchMission();
        if(styleHistory == null) {
            return Result.error("No mission now");
        } else {
            return Result.success(styleHistory);
        }
    }

    /**
     * 提交style审核
     * @param auditMap
     * @return
     */
    @PostMapping("/audit")
    @ResponseBody
    public Result auditStyle(@RequestBody Map auditMap) {
        boolean result = styleService.auditStyle(auditMap);
        if(result) {
            return Result.success("Audit style success!");
        } else {
            return Result.error("Audit style error!");
        }
    }

    /**
     * style修改冲突检测
     * @param styleName
     * @param userId
     * @return
     */
    @GetMapping("/startModify")
    @ResponseBody
    public Result startModify(@RequestParam("styleName") String styleName, @RequestParam("userId") Long userId) {
        boolean isLock = lockUtil.isLock(styleName, LockUtil.MODIFYING_STYLE);
        if(isLock) {
            return Result.error("Start modify error");
        } else {
            return Result.success("Start modify success!");
        }
    }

    /**
     * 根据enName获得style历史记录
     * @param styleName
     * @return
     */
    @GetMapping("/version/all")
    @ResponseBody
    public Result getAllStyleVersion(@RequestParam("styleName") String styleName, @RequestParam("userId")Long userId) {
        List<StyleHistory> styleHistories = styleService.getAllStyleVersion(styleName, userId);
        if(styleHistories!=null&&styleHistories.size()>0) {
            return Result.success(styleHistories);
        } else {
            return Result.error("Get all style version failed");
        }
    }
}
