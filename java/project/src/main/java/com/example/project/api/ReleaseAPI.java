package com.example.project.api;

import com.example.project.common.Paging;
import com.example.project.common.Result;
import com.example.project.dataobject.MainReleaseDO;
import com.example.project.dataobject.ReleaseDO;
import com.example.project.dataobject.ReleaseHistoryDO;
import com.example.project.model.MainRelease;
import com.example.project.model.Release;
import com.example.project.model.ReleaseHistory;
import com.example.project.service.ReleaseService;
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
 * 与release相关的操作的api
 */
@Controller
@RequestMapping("/release")
public class ReleaseAPI {

    @Autowired
    private ReleaseService releaseService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private LockUtil lockUtil;

    /**
     * 程序启动时进行初始化
     */
    @PostConstruct
    public void init() {
        releaseService.getNotAuditReleaseVersion();
    }

    /**
     * 启动后每隔五分钟进行一次感谢缓存的持久化
     */
    @Scheduled(cron = "* */5 *  * * ? ")
    public void releaseContributeThanksCount() {
        System.out.println("我开始持久化点赞数据啦");
        releaseService.releaseContributeThanksCount();
    }

    /**
     * 通过id获取release
     * @param id
     * @return
     */
    @GetMapping("/id/{id}")
    @ResponseBody
    public Result<Release> getReleaseById(@PathVariable("id") Long id) {
        Release release = releaseService.getReleaseById(id);
        if(release != null) {
            return Result.success(release);
        } else {
            return Result.error("Get release by id failed.");
        }
    }

    /**
     * 通过一组id获取一组release
     * @param ids
     * @return
     */
    @GetMapping("/ids")
    @ResponseBody
    public Result<List<Release>> getReleasesByIds(@RequestParam("ids")List<Long> ids) {
        List<Release> releases = releaseService.getReleasesByIds(ids);
        if(releases != null && releases.size() >0) {
            return Result.success(releases);
        } else {
            return Result.error("Get releases by ids failed.");
        }
    }

    /**
     * 修改release
     * @param releaseDO
     * @return
     */
    @PostMapping("/update")
    @ResponseBody
    public Result updateRelease(@RequestBody ReleaseDO releaseDO) {
        int i = releaseService.updateRelease(releaseDO);
        if(i==1) {
            return Result.success("Update release success!");
        } else {
            return Result.error("Update release failed!");
        }
    }

    /**
     * 添加release
     * @param releaseDO
     * @return
     */
    @PutMapping("/add")
    @ResponseBody
    public Result addRelease(@RequestBody ReleaseDO releaseDO) {
        Long id = releaseService.addRelease(releaseDO);
        if(id!=null) {
            return Result.success(id);
        } else {
            return Result.error("Add release failed");
        }
    }

    /**
     * release修改冲突检测
     * @param releaseId
     * @param userId
     * @return
     */
    @GetMapping("/startModify")
    @ResponseBody
    public Result startModify(@RequestParam("releaseId") Long releaseId, @RequestParam("userId") Long userId) {
        boolean isLock = lockUtil.isLock(releaseId, LockUtil.MODIFYING_RELEASE);
        if(isLock) {
            return Result.error("Start modify error");
        } else {
            return Result.success("Start modify success!");
        }
    }

    /**
     * 根据id获取mainRelease
     * @param mainId
     * @return
     */
    @GetMapping("/main/id/{mainId}")
    @ResponseBody
    public Result<MainRelease> getMainReleaseById(@PathVariable("mainId")Long mainId) {
        MainRelease mainRelease = releaseService.getMainReleaseById(mainId);
        if(mainRelease != null) {
            return Result.success(mainRelease);
        } else {
            return Result.error("Get main release by id failed!");
        }
    }

    /**
     * 根据关键词获取mainRelease
     * @param keyword
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/main/search")
    @ResponseBody
    public Result<Paging<MainRelease>> getMainReleaseByKeyword(@RequestParam("keyword")String keyword, @RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) {
        Paging<MainRelease> mainReleases = releaseService.getMainReleaseByKeyword(keyword, pageNum, pageSize);
        if(mainReleases != null) {
            return Result.success(mainReleases);
        } else {
            return Result.error("Get main release by keyword failed!");
        }
    }

    /**
     * 添加mainRelease
     * @param mainReleaseDO
     * @return
     */
    @PutMapping("/main/add")
    @ResponseBody
    public Result addMainRelease(@RequestBody MainReleaseDO mainReleaseDO) {
        Long id = releaseService.addMainRelease(mainReleaseDO);
        if(id!=null) {
            return Result.success(id);
        } else {
            return Result.error("Add main release failed");
        }
    }

    /**
     * 获取一个release审核任务
     * @return
     */
    @GetMapping("/mission")
    @ResponseBody
    public Result dispatchMission() {
        ReleaseHistory releaseHistory = releaseService.dispatchMission();
        if(releaseHistory == null) {
            return Result.error("No mission now");
        } else {
            return Result.success(releaseHistory);
        }
    }

    /**
     * 添加releaseHistory
     * @param releaseHistoryDO
     * @return
     */
    @PutMapping("/version/add")
    @ResponseBody
    public Result addReleaseVersion(@RequestBody ReleaseHistoryDO releaseHistoryDO) {
        if(releaseHistoryDO.getIsNew()!=1) {
            boolean isLock = lockUtil.isLock(releaseHistoryDO.getReleaseId(), LockUtil.MODIFYING_RELEASE);
            if (isLock) {
                return Result.error("Add release version failed!");
            }
        }
        int addNum = releaseService.addReleaseVersion(releaseHistoryDO);
        if(addNum <= 0) {
            return Result.error("Add release version failed!");
        } else {
            if(releaseHistoryDO.getIsNew()!=1) {
                lockUtil.lock(releaseHistoryDO.getReleaseId(), releaseHistoryDO.getUserId(), LockUtil.MODIFYING_RELEASE);
            }
            return Result.success("Add release version succeed!");
        }
    }

    /**
     * 更新releaseHistory
     * @param releaseHistoryDO
     * @return
     */
    @PostMapping("/version/update")
    @ResponseBody
    public Result updateReleaseVersion(@RequestBody ReleaseHistoryDO releaseHistoryDO) {
        int i = releaseService.updateReleaseVersion(releaseHistoryDO);
        if(i==1) {
            return Result.success("Audit release success!");
        } else {
            return Result.error("Audit release failed!");
        }
    }

    /**
     * 根据id获得release历史记录
     * @param releaseId
     * @return
     */
    @GetMapping("/version/all")
    @ResponseBody
    public Result getAllReleaseVersion(@RequestParam("releaseId") Long releaseId, @RequestParam("userId")Long userId) {
        List<ReleaseHistory> releaseHistories = releaseService.getAllReleaseVersion(releaseId, userId);
        if(releaseHistories!=null&&releaseHistories.size()>0) {
            return Result.success(releaseHistories);
        } else {
            return Result.error("Get all release version failed");
        }
    }

    /**
     * 提交release审核
     * @param auditMap
     * @return
     */
    @PostMapping("/audit")
    @ResponseBody
    public Result auditRelease(@RequestBody Map auditMap) {
        boolean result = releaseService.auditRelease(auditMap);
        if(result) {
            return Result.success("Audit release success!");
        } else {
            return Result.error("Audit release error!");
        }
    }

    /**
     * 根据artistId获取release总数
     * @param artistId
     * @return
     */
    @GetMapping("/artist/count/{artistId}")
    @ResponseBody
    public Result<Map> getArtistReleaseCount(@PathVariable("artistId") Long artistId) {
        return Result.success(releaseService.getArtistReleaseCount(artistId));
    }

    /**
     * 根据artistId获取release
     * @param artistId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/artist/release/{artistId}")
    @ResponseBody
    public Result<Paging<Release>> getArtistRelease(@PathVariable("artistId") Long artistId, @RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) {
        Paging<Release> res = releaseService.getArtistRelease(artistId, pageNum, pageSize);
        if(res.getData() != null) {
            return Result.success(res);
        } else {
            return Result.error("Get artist release failed.");
        }
    }

    /**
     * 根据artistId获取featured release
     * @param artistId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/artist/featured/{artistId}")
    @ResponseBody
    public Result<Paging<Release>> getArtistFeatured(@PathVariable("artistId") Long artistId, @RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) {
        Paging<Release> res = releaseService.getArtistFeatured(artistId, pageNum, pageSize);
        if(res.getData() != null) {
            return Result.success(res);
        } else {
            return Result.error("Get artist featured failed.");
        }
    }

    /**
     * 根据artistId获取behind release
     * @param artistId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/artist/behind/{artistId}")
    @ResponseBody
    public Result<Paging<Release>> getArtistBehind(@PathVariable("artistId") Long artistId, @RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) {
        Paging<Release> res = releaseService.getArtistBehind(artistId, pageNum, pageSize);
        if(res.getData() != null) {
            return Result.success(res);
        } else {
            return Result.error("Get artist behind failed!");
        }
    }

    /**
     * 根据labelId获取release
     * @param labelId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/label/{labelId}")
    @ResponseBody
    public Result<Paging<Release>> getLabelRelease(@PathVariable("labelId") Long labelId, @RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) {
        Paging<Release> result = releaseService.getLabelRelease(labelId, pageNum, pageSize);
        if(result.getData() != null) {
            return Result.success(result);
        } else {
            return Result.error("Get label releases failed!");
        }

    }
}
