package com.example.project.api;

import com.example.project.common.Result;
import com.example.project.dataobject.ArtistHistoryDO;
import com.example.project.dataobject.LabelHistoryDO;
import com.example.project.model.ArtistHistory;
import com.example.project.model.Label;
import com.example.project.model.LabelHistory;
import com.example.project.service.LabelService;
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
 * 与label相关的操作的api
 */
@Controller
@RequestMapping("/label")
public class LabelAPI {

    @Autowired
    private LabelService labelService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private LockUtil lockUtil;

    /**
     * 程序启动时进行初始化
     */
    @PostConstruct
    public void init() {
        labelService.getNotAuditLabelVersion();
    }

    /**
     * 启动后每隔五分钟进行一次感谢缓存的持久化
     */
    @Scheduled(cron = "* */5 *  * * ? ")
    public void labelContributeThanksCount() {
        System.out.println("我开始持久化点赞数据啦");
        labelService.labelContributeThanksCount();
    }

    /**
     * 通过id获取label
     * @param id
     * @return
     */
    @GetMapping("/id/{id}")
    @ResponseBody
    public Result<Label> getLabelById(@PathVariable("id") Long id) {
        Label result = labelService.getLabelById(id);
        if(result != null) {
            return Result.success(result);
        } else {
            return Result.error("Get label by id failed");
        }
    }

    /**
     * 通过一组id获取一组label
     * @param ids
     * @return
     */
    @GetMapping("/ids")
    @ResponseBody
    public Result<List<Label>> getLabelsByIds(@RequestParam("ids")List<Long> ids) {
        List<Label> result = labelService.getLabelsByIds(ids);
        if(result != null && result.size() > 0) {
            return Result.success(result);
        } else {
            return Result.error("Get labels by ids failed");
        }
    }

    /**
     * 添加labelHistory
     * @param labelHistoryDO
     * @return
     */
    @PutMapping("/version/add")
    @ResponseBody
    public Result addLabelVersion(@RequestBody LabelHistoryDO labelHistoryDO) {
        if(labelHistoryDO.getIsNew()!=1) {
            boolean isLock = lockUtil.isLock(labelHistoryDO.getLabelId(), LockUtil.MODIFYING_LABEL);
            if (isLock) {
                return Result.error("Add label version failed!");
            }
        }
        int addNum = labelService.addLabelVersion(labelHistoryDO);
        if(addNum <= 0) {
            return Result.error("Add label version failed!");
        } else {
            if(labelHistoryDO.getIsNew()!=1) {
                lockUtil.lock(labelHistoryDO.getLabelId(), labelHistoryDO.getUserId(), LockUtil.MODIFYING_LABEL);
            }
            return Result.success("Add label version succeed!");
        }
    }

    /**
     * 获取一个label审核任务
     * @return
     */
    @GetMapping("/mission")
    @ResponseBody
    public Result dispatchMission() {
        LabelHistory labelHistory = labelService.dispatchMission();
        if(labelHistory == null) {
            return Result.error("No mission now");
        } else {
            return Result.success(labelHistory);
        }
    }

    /**
     * 提交label审核
     * @param auditMap
     * @return
     */
    @PostMapping("/audit")
    @ResponseBody
    public Result auditLabel(@RequestBody Map auditMap) {
        boolean result = labelService.auditLabel(auditMap);
        if(result) {
            return Result.success("Audit label success!");
        } else {
            return Result.error("Audit label error!");
        }
    }

    /**
     * label修改冲突检测
     * @param labelId
     * @param userId
     * @return
     */
    @GetMapping("/startModify")
    @ResponseBody
    public Result startModify(@RequestParam("labelId") Long labelId, @RequestParam("userId") Long userId) {
        boolean isLock = lockUtil.isLock(userId, LockUtil.MODIFYING_LABEL);
        if(isLock) {
            return Result.error("Start modify error");
        } else {
            return Result.success("Start modify success!");
        }
    }

    /**
     * 根据id获得label历史记录
     * @param labelId
     * @return
     */
    @GetMapping("/version/all")
    @ResponseBody
    public Result getAllLabelVersion(@RequestParam("labelId") Long labelId, @RequestParam("userId")Long userId) {
        List<LabelHistory> labelHistories = labelService.getAllLabelVersion(labelId, userId);
        if(labelHistories!=null&&labelHistories.size()>0) {
            return Result.success(labelHistories);
        } else {
            return Result.error("Get all label version failed");
        }
    }
}
