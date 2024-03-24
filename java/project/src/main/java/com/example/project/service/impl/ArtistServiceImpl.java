package com.example.project.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.example.project.dao.ArtistDAO;
import com.example.project.dao.ArtistHistoryDAO;
import com.example.project.dataobject.*;
import com.example.project.model.Artist;
import com.example.project.model.ArtistHistory;
import com.example.project.service.ArtistService;
import com.example.project.util.KeywordUtil;
import com.example.project.util.RedisOperationUtils.AuditorUtil;
import com.example.project.util.RedisOperationUtils.ContributeUtil;
import com.example.project.util.RedisOperationUtils.LockUtil;
import com.example.project.util.RedisOperationUtils.ThankUtil;
import com.example.project.util.delayedQueue.DelayedQueue;
import com.example.project.util.delayedQueue.DelayedQueueListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ArtistServiceImpl implements ArtistService {

    @Autowired
    private ArtistDAO artistDAO;

    @Autowired
    private ArtistHistoryDAO artistHistoryDAO;

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
    public Artist getArtistById(Long id) {
        ArtistDO artistDO = artistDAO.getArtistById(id);
        if(artistDO == null) {
            return null;
        }
        return artistDO.toModel();
    }

    @Override
    public List<Artist> getArtistsByIds(List<Long> ids) {
        List<ArtistDO> artistDOs = artistDAO.getArtistsByIds(ids);
        return artistDOs.stream().map(ArtistDO::toModel).collect(Collectors.toList());
    }

    @Override
    public int addArtistVersion(ArtistHistoryDO artistHistoryDO) {
        ArtistHistory artistHistory = artistHistoryDO.toModel();
        if(artistHistoryDO.getArtistId() != null) {
            artistHistory.setArtist(getArtistById(artistHistory.getArtist().getId()));
        }
        int result = artistHistoryDAO.addArtistVersion(artistHistoryDO);
        artistHistory.setId(artistHistoryDO.getId());
        redisTemplate.opsForList().leftPush(KeywordUtil.ARTIST_NOT_AUDIT, artistHistory);
        return result;
    }

    @Override
    public void getNotAuditArtistVersion() {
        redisTemplate.delete(KeywordUtil.ARTIST_NOT_AUDIT);
        List<ArtistHistoryDO> artistHistoryDOs = artistHistoryDAO.getNotAuditArtistVersion();
        if(artistHistoryDOs!=null&&artistHistoryDOs.size()>0) {
            List<ArtistHistory> artistHistories = artistHistoryDOs.stream().map(artistHistoryDO -> {
                ArtistHistory artistHistory = artistHistoryDO.toModel();
                if(artistHistoryDO.getArtistId() != null) {
                    artistHistory.setArtist(getArtistById(artistHistory.getArtist().getId()));
                }
                return artistHistory;
            }).collect(Collectors.toList());
            redisTemplate.opsForList().leftPushAll(KeywordUtil.ARTIST_NOT_AUDIT, artistHistories);
        }
    }

    @Override
    public ArtistHistory dispatchMission() {
        ArtistHistory artistHistory = (ArtistHistory) redisTemplate.opsForList().rightPop(KeywordUtil.ARTIST_NOT_AUDIT);
        if(artistHistory != null) {
            System.out.println(LocalDateTime.now()+"，"+artistHistory.getId()+"审核任务被派发");
            delayedQueue.addQueue(artistHistory, 15, TimeUnit.MINUTES, RHQueueListener.class.getName());
        }
        return artistHistory;
    }

    @Override
    public int updateArtistVersion(ArtistHistoryDO artistHistoryDO) {
        return artistHistoryDAO.updateArtistVersion(artistHistoryDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean auditArtist(Map<String, Object> dataMap) {
        try {
            ArtistDO artistData = JSONObject.parseObject((String) dataMap.get("artistData"), ArtistDO.class);
            ArtistHistoryDO historyData = JSONObject.parseObject((String) dataMap.get("historyData"), ArtistHistoryDO.class);

            if(historyData.getIsNew() == 1) {
                artistDAO.addArtist(artistData);
                historyData.setArtistId(artistData.getId());
            } else if(historyData.getIsNew() == 0) {
                artistDAO.updateArtist(artistData);
            }
            artistHistoryDAO.updateArtistVersion(historyData);
            lockUtil.unlock(artistData.getId(), LockUtil.MODIFYING_ARTIST);
            auditCountUtil.newAudit(AuditorUtil.ARTISTS, historyData.getId(), historyData.getAuditorId());

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
    public List<ArtistHistory> getAllArtistVersion(Long artistId, Long userId) {
        List<ArtistHistoryDO> artistHistoryDOs = artistHistoryDAO.getAllArtistVersion(artistId);
        List<ArtistHistory> artistHistories = artistHistoryDOs.stream().map(artistHistoryDO -> {
            ArtistHistory artistHistory = artistHistoryDO.toModel();
            artistHistory.setThanks(thankUtil.getThankCount(artistHistory.getId(), ThankUtil.ARTIST_AREA));
            artistHistory.setThank(thankUtil.getIsThank(artistHistory.getId(), userId, ThankUtil.ARTIST_AREA));
            return artistHistory;
        }).collect(Collectors.toList());



        return artistHistories;
    }

    @Override
    public void artistContributeThanksCount() {
        Map thankMap = thankUtil.contributeThankCount(ThankUtil.ARTIST_AREA);
        if(thankMap!=null&&thankMap.size()>0) {
            artistHistoryDAO.updateArtistThanks(thankMap);
        }
    }

    public void getAndSaveThanksCount() {

    }

    @Component
    public class RHQueueListener implements DelayedQueueListener<ArtistHistory> {
        Logger logger = LoggerFactory.getLogger(RHQueueListener.class);

        @Autowired
        private ArtistHistoryDAO artistHistoryDAO;

        @Autowired
        private RedisTemplate redisTemplate;

        @Override
        public void invoke(ArtistHistory artistHistory) {
            int isPass = artistHistoryDAO.isAudit(artistHistory.getId());
            //该条目未被审核
            if(isPass==0) {
                logger.info("我执行完毕了，条目未被审核");
                redisTemplate.opsForList().leftPush(KeywordUtil.ARTIST_NOT_AUDIT, artistHistory);
                //该条目已经被审核
            } else {
                if(isPass==1||isPass==2) {
                    logger.info("我执行完毕了，条目已被审核");
                } else {
                    logger.info("我执行完毕了，但是出现错误");
                    redisTemplate.opsForList().leftPush(KeywordUtil.ARTIST_NOT_AUDIT, artistHistory);
                }
            }
        }
    }
}
