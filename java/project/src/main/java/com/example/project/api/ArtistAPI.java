package com.example.project.api;

import com.example.project.common.Result;
import com.example.project.dataobject.ArtistHistoryDO;
import com.example.project.dataobject.ReleaseHistoryDO;
import com.example.project.model.Artist;
import com.example.project.model.ArtistHistory;
import com.example.project.model.ReleaseHistory;
import com.example.project.service.ArtistService;
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
 * 与artist相关的操作的api
 */
@Controller
@RequestMapping("/artist")
public class ArtistAPI {

    @Autowired
    private ArtistService artistService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private LockUtil lockUtil;

    /**
     * 程序启动时进行初始化
     */
    @PostConstruct
    public void init() {
        artistService.getNotAuditArtistVersion();
    }

    /**
     * 启动后每隔五分钟进行一次感谢缓存的持久化
     */
    @Scheduled(cron = "* */5 *  * * ? ")
    public void artistContributeThanksCount() {
        artistService.artistContributeThanksCount();
    }

    /**
     * 通过id获取artist
     * @param id
     * @return
     */
    @GetMapping("/id/{id}")
    @ResponseBody
    public Result<Artist> getArtistById(@PathVariable("id") Long id) {
        Artist artist = artistService.getArtistById(id);
        if(artist != null) {
            return Result.success(artist);
        } else {
            return Result.error("Get artist by id failed");
        }
    }

    /**
     * 通过一组id获取一组artist
     * @param ids
     * @return
     */
    @GetMapping("/ids")
    @ResponseBody
    public Result<List<Artist>> getArtistsByIds(@RequestParam("ids")List<Long> ids) {
        List<Artist> artists = artistService.getArtistsByIds(ids);
        if(artists != null && artists.size() > 0) {
            return Result.success(artists);
        } else {
            return Result.error("Get artists by ids failed");
        }
    }

    /**
     * 添加artistHistory
     * @param artistHistoryDO
     * @return
     */
    @PutMapping("/version/add")
    @ResponseBody
    public Result addArtistVersion(@RequestBody ArtistHistoryDO artistHistoryDO) {
        if(artistHistoryDO.getIsNew()!=1) {
            boolean isLock = lockUtil.isLock(artistHistoryDO.getArtistId(), LockUtil.MODIFYING_ARTIST);
            if(isLock) {
                return Result.error("Add artist version failed!");
            }
        }
        int addNum = artistService.addArtistVersion(artistHistoryDO);
        if(addNum <= 0) {
            return Result.error("Add release version failed!");
        } else {
            if(artistHistoryDO.getIsNew()!=1) {
                lockUtil.lock(artistHistoryDO.getArtistId(), artistHistoryDO.getUserId(), LockUtil.MODIFYING_ARTIST);
            }
            return Result.success("Add release version succeed!");
        }
    }

    /**
     * 获取一个artist审核任务
     * @return
     */
    @GetMapping("/mission")
    @ResponseBody
    public Result dispatchMission() {
        ArtistHistory artistHistory = artistService.dispatchMission();
        if(artistHistory == null) {
            return Result.error("No mission now");
        } else {
            return Result.success(artistHistory);
        }
    }

    /**
     * 提交artist审核
     * @param auditMap
     * @return
     */
    @PostMapping("/audit")
    @ResponseBody
    public Result auditArtist(@RequestBody Map auditMap) {
        boolean result = artistService.auditArtist(auditMap);
        if(result) {
            return Result.success("Audit artist success!");
        } else {
            return Result.error("Audit artist error!");
        }
    }

    /**
     * artist修改冲突检测
     * @param artistId
     * @param userId
     * @return
     */
    @GetMapping("/startModify")
    @ResponseBody
    public Result startModify(@RequestParam("artistId") Long artistId, @RequestParam("userId") Long userId) {
        boolean isLock = lockUtil.isLock(artistId, LockUtil.MODIFYING_ARTIST);
        if(isLock) {
            return Result.error("Start modify error");
        } else {
            return Result.success("Start modify success!");
        }
    }

    /**
     * 根据id获得artist历史记录
     * @param artistId
     * @return
     */
    @GetMapping("/version/all")
    @ResponseBody
    public Result getAllArtistVersion(@RequestParam("artistId") Long artistId, @RequestParam("userId")Long userId) {
        List<ArtistHistory> artistHistories = artistService.getAllArtistVersion(artistId, userId);
        if(artistHistories!=null&&artistHistories.size()>0) {
            return Result.success(artistHistories);
        } else {
            return Result.error("Get all release version failed");
        }
    }
}
