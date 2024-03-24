package com.example.project.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.example.project.dao.LabelDAO;
import com.example.project.dao.LabelHistoryDAO;
import com.example.project.dataobject.LabelDO;
import com.example.project.dataobject.LabelHistoryDO;
import com.example.project.model.Label;
import com.example.project.model.LabelHistory;
import com.example.project.service.LabelService;
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
public class LabelServiceImpl implements LabelService {

    @Autowired
    private LabelDAO labelDAO;

    @Autowired
    private LabelHistoryDAO labelHistoryDAO;

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
    public Label getLabelById(Long id) {
        LabelDO labelDO = labelDAO.getLabelById(id);

        if(labelDO == null) {
            return null;
        }

        Label label = labelDO.toModel();

        List<LabelDO> childrenLabelDO = labelDAO.getChildrenById(label.getId());
        List<Label> childrenLabel = childrenLabelDO.stream().map(LabelDO::toModel).collect(Collectors.toList());

        if(childrenLabel.size() == 0) {
            return label;
        }
        label.setChildren(childrenLabel);
        return label;
    }

    @Override
    public List<Label> getLabelsByIds(List<Long> ids) {
        List<LabelDO> labelDOs = labelDAO.getLabelsByIds(ids);
        return labelDOs.stream().map(LabelDO::toModel).collect(Collectors.toList());
    }

    @Override
    public int addLabelVersion(LabelHistoryDO labelHistoryDO) {
        LabelHistory labelHistory = labelHistoryDO.toModel();
        if(labelHistoryDO.getLabelId() != null) {
            labelHistory.setLabel(getLabelById(labelHistory.getLabel().getId()));
        }
        int result = labelHistoryDAO.addLabelVersion(labelHistoryDO);
        labelHistory.setId(labelHistoryDO.getId());
        redisTemplate.opsForList().leftPush(KeywordUtil.LABEL_NOT_AUDIT, labelHistory);
        return result;
    }

    @Override
    public void getNotAuditLabelVersion() {
        redisTemplate.delete(KeywordUtil.LABEL_NOT_AUDIT);
        List<LabelHistoryDO> labelHistoryDOs  = labelHistoryDAO.getNotAuditLabelVersion();
        if(labelHistoryDOs!=null&&labelHistoryDOs.size()>0) {
            List<LabelHistory> labelHistories = labelHistoryDOs.stream().map(labelHistoryDO -> {
                LabelHistory labelHistory = labelHistoryDO.toModel();
                if(labelHistoryDO.getLabelId() != null) {
                    labelHistory.setLabel(getLabelById(labelHistory.getLabel().getId()));
                }
                return labelHistory;
            }).collect(Collectors.toList());
            redisTemplate.opsForList().leftPushAll(KeywordUtil.LABEL_NOT_AUDIT, labelHistories);
        }
    }

    @Override
    public LabelHistory dispatchMission() {
        LabelHistory labelHistory = (LabelHistory) redisTemplate.opsForList().rightPop(KeywordUtil.LABEL_NOT_AUDIT);
        if(labelHistory != null) {
            System.out.println(LocalDateTime.now()+"，"+labelHistory.getId()+"审核任务被派发");
            delayedQueue.addQueue(labelHistory, 15, TimeUnit.MINUTES, RHQueueListener.class.getName());
        }
        return labelHistory;
    }

    @Override
    public int updateLabelVersion(LabelHistoryDO labelHistoryDO) {
        return labelHistoryDAO.updateLabelVersion(labelHistoryDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean auditLabel(Map<String, Object> dataMap) {
        try {
            LabelDO labelData = JSONObject.parseObject((String) dataMap.get("labelData"), LabelDO.class);
            LabelHistoryDO historyData = JSONObject.parseObject((String) dataMap.get("historyData"), LabelHistoryDO.class);

            if(historyData.getIsNew() == 1) {
                labelDAO.addLabel(labelData);
                historyData.setLabelId(labelData.getId());
            } else if(historyData.getIsNew() == 0) {
                labelDAO.updateLabel(labelData);
            }
            labelHistoryDAO.updateLabelVersion(historyData);
            lockUtil.unlock(labelData.getId(), LockUtil.MODIFYING_LABEL);
            auditCountUtil.newAudit(AuditorUtil.LABELS, historyData.getId(), historyData.getAuditorId());

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
    public List<LabelHistory> getAllLabelVersion(Long labelId, Long userId) {
        List<LabelHistoryDO> labelHistoryDOs = labelHistoryDAO.getAllLabelVersion(labelId);
        List<LabelHistory> labelHistories = labelHistoryDOs.stream().map(labelHistoryDO -> {
            LabelHistory labelHistory = labelHistoryDO.toModel();
            labelHistory.setThanks(thankUtil.getThankCount(labelHistory.getId(), ThankUtil.LABEL_AREA));
            labelHistory.setThank(thankUtil.getIsThank(labelHistory.getId(), userId, ThankUtil.LABEL_AREA));
            return labelHistory;
        }).collect(Collectors.toList());

        return labelHistories;
    }

    @Override
    public void labelContributeThanksCount() {
        Map thankMap = thankUtil.contributeThankCount(ThankUtil.LABEL_AREA);
        if(thankMap!=null&&thankMap.size()>0) {
            labelHistoryDAO.updateLabelThanks(thankMap);
        }
    }


    @Component
    public class RHQueueListener implements DelayedQueueListener<LabelHistory> {
        Logger logger = LoggerFactory.getLogger(RHQueueListener.class);

        @Autowired
        private LabelHistoryDAO labelHistoryDAO;

        @Autowired
        private RedisTemplate redisTemplate;

        @Override
        public void invoke(LabelHistory labelHistory) {

            int isPass = labelHistoryDAO.isAudit(labelHistory.getId());
            //该条目未被审核
            if(isPass==0) {
                logger.info("我执行完毕了，条目未被审核");
                redisTemplate.opsForList().leftPush(KeywordUtil.LABEL_NOT_AUDIT, labelHistory);
                //该条目已经被审核
            } else {
                if(isPass==1||isPass==2) {
                    logger.info("我执行完毕了，条目已被审核");
                } else {
                    logger.info("我执行完毕了，但是出现错误");
                    redisTemplate.opsForList().leftPush(KeywordUtil.LABEL_NOT_AUDIT, labelHistory);
                }
            }
        }
    }
}
