package com.example.project.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.example.project.common.Paging;
import com.example.project.dao.MainReleaseDAO;
import com.example.project.dao.ReleaseDAO;
import com.example.project.dao.ReleaseHistoryDAO;
import com.example.project.dataobject.MainReleaseDO;
import com.example.project.dataobject.ReleaseDO;
import com.example.project.dataobject.ReleaseHistoryDO;
import com.example.project.model.MainRelease;
import com.example.project.model.Release;
import com.example.project.model.ReleaseHistory;
import com.example.project.service.ReleaseService;
import com.example.project.util.KeywordUtil;
import com.example.project.util.RedisOperationUtils.*;
import com.example.project.util.delayedQueue.DelayedQueue;
import com.example.project.util.delayedQueue.DelayedQueueListener;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ReleaseServiceImpl implements ReleaseService {
    @Autowired
    private ReleaseDAO releaseDAO;

    @Autowired
    private MainReleaseDAO mainReleaseDAO;

    @Autowired
    private ReleaseHistoryDAO releaseHistoryDAO;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private LockUtil lockUtil;

    @Autowired
    private ThankUtil thankUtil;

    @Autowired
    private AuditorUtil auditCountUtil;

    @Autowired
    private ContributeUtil contributeUtil;

    @Autowired
    private DelayedQueue delayedQueue;

    @Override
    public Release getReleaseById(Long id) {
        ReleaseDO releaseDO = releaseDAO.getReleaseById(id);
        if(releaseDO == null) {
            return null;
        }
        Release release = releaseDO.toModel();
        if(release.getCredits() == null || release.getCredits().size() == 0) {
            release.setCredits(null);
        }
        if(release.getMainRelease().getId()!= null) {
            MainRelease mainRelease = getMainReleaseById(release.getMainRelease().getId());
            if(mainRelease!=null) {
                release.setMainRelease(mainRelease);
            } else {
                release.setMainRelease(null);
            }
        }

        List<Map> otherVersions = getOtherVersions(releaseDO);

        release.setOtherVersion(otherVersions);
        return release;
    }

    @Override
    public List<Release> getReleasesByIds(List<Long> ids) {
        List<ReleaseDO> releaseDOs = releaseDAO.getReleasesByIds(ids);
        List<Release> releases = releaseDOs.stream().map(r-> {
            Release release = r.toModel();
            if(release.getCredits().size() == 0) {
                release.setCredits(null);
            }
            return release;
        }).collect(Collectors.toList());
        return releases;
    }

    @Override
    public Map getArtistReleaseCount(Long artistId) {
        Map res = new HashMap<>();

        res.put("release", releaseDAO.getArtistReleasesCount(artistId));
        res.put("featured", releaseDAO.getArtistFeaturedCount(artistId));
        res.put("behind", releaseDAO.getArtistBehindCount(artistId));

        return res;
    }

    @Override
    public Paging<Release> getArtistRelease(Long artistId, int pageNum, int pageSize) {
        Page<ReleaseDO> page = PageHelper.startPage(pageNum, pageSize).doSelectPage(()-> releaseDAO.getArtistRelease(artistId));
        List<ReleaseDO> releaseDOs = page.getResult();

        List<Release> releases = releaseDOs.stream().map(ReleaseDO::toModel).collect(Collectors.toList());

        return new Paging<>(page.getPageNum(), page.getPageSize(), page.getPages(), page.getTotal(), releases);
    }

    @Override
    public Paging<Release> getArtistFeatured(Long artistId, int pageNum, int pageSize) {
        Page<ReleaseDO> page = PageHelper.startPage(pageNum, pageSize).doSelectPage(()-> releaseDAO.getArtistFeatured(artistId));
        List<ReleaseDO> releaseDOs = page.getResult();

        List<Release> releases = releaseDOs.stream().map(ReleaseDO::toModel).collect(Collectors.toList());

        return new Paging<>(page.getPageNum(), page.getPageSize(), page.getPages(), page.getTotal(), releases);
    }

    @Override
    public Paging<Release> getArtistBehind(Long artistId, int pageNum, int pageSize) {
        Page<ReleaseDO> page = PageHelper.startPage(pageNum, pageSize).doSelectPage(()-> releaseDAO.getArtistBehind(artistId));
        List<ReleaseDO> releaseDOs = page.getResult();

        List<Release> releases = releaseDOs.stream().map(ReleaseDO::toModel).collect(Collectors.toList());

        return new Paging<>(page.getPageNum(), page.getPageSize(), page.getPages(), page.getTotal(), releases);
    }

    @Override
    public Paging<Release> getLabelRelease(Long labelId, int pageNum, int pageSize) {
        Page<ReleaseDO> page = PageHelper.startPage(pageNum, pageSize).doSelectPage(()-> releaseDAO.getLabelRelease(labelId));
        List<ReleaseDO> releaseDOs = page.getResult();

        List<Release> releases = releaseDOs.stream().map(ReleaseDO::toModel).collect(Collectors.toList());

        return new Paging<>(page.getPageNum(), page.getPageSize(), page.getPages(), page.getTotal(), releases);
    }

    private List<Map> getOtherVersions(ReleaseDO releaseDO) {
        if(releaseDO.getMainId() == null) {
            return null;
        }
        List<ReleaseDO> releaseDOs = releaseDAO.getReleaseVersions(releaseDO.getMainId());
        return releaseDOs.stream().map(r-> {
            Map otherVersion = new HashMap<>();
            Release r2 = r.toModel();
            otherVersion.put("id", r2.getId());
            otherVersion.put("title", r2.getTitle());
            otherVersion.put("releaseDate", r2.getReleaseDate());
            return otherVersion;
        }).collect(Collectors.toList());
    }


    @Override
    public MainRelease getMainReleaseById(Long id) {
        MainReleaseDO mainReleaseDO = mainReleaseDAO.getMainReleaseById(id);
        if(mainReleaseDO == null) {
            return null;
        }
        MainRelease mainRelease = mainReleaseDO.toModel();

        return mainRelease;
    }

    @Override
    public Paging<MainRelease> getMainReleaseByKeyword(String keyword, int pageNum, int pageSize) {
        Page<MainReleaseDO> page = PageHelper.startPage(pageNum, pageSize).doSelectPage(()-> mainReleaseDAO.search(keyword));
        List<MainReleaseDO> mainReleaseDOs = page.getResult();

        List<MainRelease> mainReleases = mainReleaseDOs.stream().map(MainReleaseDO::toModel).collect(Collectors.toList());

        return new Paging<>(page.getPageNum(), page.getPageSize(), page.getPages(), page.getTotal(), mainReleases);
    }

    @Override
    public Long addMainRelease(MainReleaseDO mainReleaseDO) {
        mainReleaseDAO.addMainRelease(mainReleaseDO);
        return mainReleaseDO.getId();
    }

    @Override
    public int addReleaseVersion(ReleaseHistoryDO releaseHistoryDO) {
        ReleaseHistory releaseHistory = releaseHistoryDO.toModel();
        if(releaseHistoryDO.getReleaseId() != null) {
            releaseHistory.setRelease(getReleaseById(releaseHistory.getRelease().getId()));
        }
        int result = releaseHistoryDAO.addReleaseVersion(releaseHistoryDO);
        releaseHistory.setId(releaseHistoryDO.getId());
        System.out.println(releaseHistory.getId());
        redisTemplate.opsForList().leftPush(KeywordUtil.RELEASE_NOT_AUDIT, releaseHistory);
        return result;
    }

    /**
     * 程序初始化的方法，获取所有未被审核的条目并存入redis
     */
    @Override
    public void getNotAuditReleaseVersion() {
        redisTemplate.delete(KeywordUtil.RELEASE_NOT_AUDIT);
        List<ReleaseHistoryDO> releaseHistoryDOs = releaseHistoryDAO.getNotAuditReleaseVersion();
        if(releaseHistoryDOs!=null&&releaseHistoryDOs.size()>0) {
            List<ReleaseHistory> releaseHistories = releaseHistoryDOs.stream().map(releaseHistoryDO -> {
                ReleaseHistory releaseHistory = releaseHistoryDO.toModel();
                if(releaseHistoryDO.getReleaseId() != null) {
                    releaseHistory.setRelease(getReleaseById(releaseHistory.getRelease().getId()));
                }
                return releaseHistory;
            }).collect(Collectors.toList());
            redisTemplate.opsForList().leftPushAll(KeywordUtil.RELEASE_NOT_AUDIT, releaseHistories);
        }
    }

    @Override
    public ReleaseHistory dispatchMission() {
        ReleaseHistory releaseHistory = (ReleaseHistory) redisTemplate.opsForList().rightPop(KeywordUtil.RELEASE_NOT_AUDIT);
        if(releaseHistory != null) {
            System.out.println(LocalDateTime.now()+"，"+releaseHistory.getId()+"审核任务被派发");
            delayedQueue.addQueue(releaseHistory, 15, TimeUnit.MINUTES, RHQueueListener.class.getName());
        }
        return releaseHistory;
    }

    @Override
    public int updateReleaseVersion(ReleaseHistoryDO releaseHistoryDO) {
        return releaseHistoryDAO.updateReleaseVersion(releaseHistoryDO);
    }

    @Override
    public int updateRelease(ReleaseDO releaseDO) {
        return releaseDAO.updateRelease(releaseDO);
    }

    @Override
    public Long addRelease(ReleaseDO releaseDO) {
        releaseDAO.addRelease(releaseDO);
        return releaseDO.getId();
    }

    @Override
    public List<ReleaseHistory> getAllReleaseVersion(Long releaseId, Long userId) {
        List<ReleaseHistoryDO> releaseHistoryDOs = releaseHistoryDAO.getAllReleaseVersion(releaseId);
        List<ReleaseHistory> releaseHistories = releaseHistoryDOs.stream().map(releaseHistoryDO -> {
            ReleaseHistory releaseHistory = releaseHistoryDO.toModel();
            releaseHistory.setThanks(thankUtil.getThankCount(releaseHistory.getId(), ThankUtil.RELEASE_AREA));
            releaseHistory.setThank(thankUtil.getIsThank(releaseHistory.getId(), userId, ThankUtil.RELEASE_AREA));
            return releaseHistory;
        }).collect(Collectors.toList());

        return releaseHistories;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean auditRelease(Map<String, Object> dataMap) {
        try {
            String main = (String) dataMap.get("mainData");
            ReleaseDO releaseData = JSONObject.parseObject((String) dataMap.get("releaseData"), ReleaseDO.class);
            ReleaseHistoryDO historyData = JSONObject.parseObject((String) dataMap.get("historyData"), ReleaseHistoryDO.class);

            if(main != null) {
                MainReleaseDO mainData = JSONObject.parseObject(main, MainReleaseDO.class);
                mainReleaseDAO.addMainRelease(mainData);
                releaseData.setMainId(mainData.getId());
            }

            if(historyData.getIsNew() == 1) {
                releaseDAO.addRelease(releaseData);
                historyData.setReleaseId(releaseData.getId());
            } else if(historyData.getIsNew() == 0) {
                releaseDAO.updateRelease(releaseData);
            }

            releaseHistoryDAO.updateReleaseVersion(historyData);
            lockUtil.unlock(releaseData.getId(), LockUtil.MODIFYING_RELEASE);
            auditCountUtil.newAudit(AuditorUtil.RELEASES, historyData.getId(), historyData.getAuditorId());

            int pt;
            if(historyData.getIsNew() == 1) {
                pt = 5;
            } else {
                pt = 3;
            }
            contributeUtil.contributePointAdd(historyData.getUserId(), pt);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void releaseContributeThanksCount() {
        Map thankMap = thankUtil.contributeThankCount(ThankUtil.RELEASE_AREA);
        if(thankMap!=null&&thankMap.size()>0) {
            releaseHistoryDAO.updateReleaseThanks(thankMap);
        }
    }


    @Component
    public class RHQueueListener implements DelayedQueueListener<ReleaseHistory> {
        Logger logger = LoggerFactory.getLogger(RHQueueListener.class);

        @Autowired
        private ReleaseHistoryDAO releaseHistoryDAO;

        @Autowired
        private RedisTemplate redisTemplate;

        @Override
        public void invoke(ReleaseHistory releaseHistory) {
            int isPass = releaseHistoryDAO.isAudit(releaseHistory.getId());
            //该条目未被审核
            if(isPass==0) {
                logger.info("我执行完毕了，条目未被审核");
                redisTemplate.opsForList().leftPush(KeywordUtil.RELEASE_NOT_AUDIT, releaseHistory);
                //该条目已经被审核
            } else {
                if(isPass==1||isPass==2) {
                    logger.info("我执行完毕了，条目已被审核");
                } else {
                    logger.info("我执行完毕了，但是出现错误");
                    redisTemplate.opsForList().leftPush(KeywordUtil.RELEASE_NOT_AUDIT, releaseHistory);
                }
            }
        }
    }
}
