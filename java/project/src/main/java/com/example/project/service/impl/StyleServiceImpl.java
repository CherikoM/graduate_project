package com.example.project.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.example.project.dao.StyleDAO;
import com.example.project.dao.StyleHistoryDAO;
import com.example.project.dataobject.StyleDO;
import com.example.project.dataobject.StyleHistoryDO;
import com.example.project.model.Style;
import com.example.project.model.StyleHistory;
import com.example.project.service.StyleService;
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
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class StyleServiceImpl implements StyleService {
    @Autowired
    private StyleDAO styleDAO;

    @Autowired
    private StyleHistoryDAO styleHistoryDAO;

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
    public List<Map> getAllGenre() {
        List<StyleDO> styleDOs = styleDAO.getAllGenre();
        return styleDOs.stream().map(styleDO -> {
            Map map = new HashMap<>();
            map.put("name", styleDO.getName());
            map.put("enName", styleDO.getEnName());
            map.put("otherName", styleDO.getOtherName());
            map.put("type", styleDO.getType());
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Map> getAllStyle() {
        List<StyleDO> styleDOs = styleDAO.getAllStyle();
        return styleDOs.stream().map(styleDO -> {
            Map map = new HashMap<>();
            map.put("name", styleDO.getName());
            map.put("enName", styleDO.getEnName());
            map.put("otherName", styleDO.getOtherName());
            map.put("type", styleDO.getType());
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Map> getGenreAndPrimary() {
        List<StyleDO> styleDOs = styleDAO.getGenreAndPrimary();
        return styleDOs.stream().map(styleDO -> {
            Map map = new HashMap<>();
            map.put("name", styleDO.getName());
            map.put("enName", styleDO.getEnName());
            map.put("otherName", styleDO.getOtherName());
            map.put("type", styleDO.getType());
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Style> styleGrouper() {
        List<StyleDO> allStyleDOs = styleDAO.getAll();
        List<Style> allStyles = allStyleDOs.stream().map(StyleDO::toModel).collect(Collectors.toList());

        Map<String, Style> styleMap = new HashMap<>();
        Style newStyle = new Style();
        newStyle.setBranch(new ArrayList<>());
        styleMap.put("", newStyle);
        allStyles.forEach(style -> styleMap.put(style.getEnName(), style));

        allStyles.forEach(style -> {
            if(style.getType().equals(Style.GENRE)) {
                styleMap.get("").getBranch().add(style);
            } else {
                List<String> belongName = style.getBelong();
                for (String s : belongName) {
                    Style base = styleMap.get(s);
                    if(
                        (base.getType().equals(Style.GENRE)
                        && style.getType().equals(Style.PRIMARY_STYLE))
                        || (base.getType().equals(Style.PRIMARY_STYLE)
                        && style.getType().equals(Style.SUBDIVIDE_STYLE))
                    ) {
                        if(base.getBranch() == null) {
                            base.setBranch(new ArrayList<>());
                        }
                        base.getBranch().add(style);
                    }
                }
            }
        });

        List<Style> styleGroup = styleMap.get("").getBranch();

        return styleGroup;
    }

    @Override
    public Style getStyleByEnName(String enName) {
        List<StyleDO> styleDOs = styleDAO.getStyleByEnName(enName);
        if(styleDOs.size()==1) {
            StyleDO styleDO = styleDOs.get(0);
            Style style = styleDO.toModel();
            List<StyleDO> branch = styleDAO.getStyleBranch(style.getEnName());
            style.setBranch(branch.stream().map(StyleDO::toModel).collect(Collectors.toList()));
            return style;
        } else {
            return null;
        }
    }

    @Override
    public Style getStyleById(Long id) {
        StyleDO styleDO = styleDAO.getStyleById(id);
        if(styleDO == null) {
            return null;
        }
        return styleDO.toModel();
    }

    @Override
    public int addStyleVersion(StyleHistoryDO styleHistoryDO) {
        StyleHistory styleHistory = styleHistoryDO.toModel();
        if(styleHistoryDO.getId() !=null) {
            styleHistory.setStyle(getStyleById(styleHistoryDO.getId()));
        }
        int result = styleHistoryDAO.addStyleVersion(styleHistoryDO);
        styleHistory.setId(styleHistoryDO.getId());
        if(result == 1) {
            redisTemplate.opsForList().leftPush(KeywordUtil.STYLE_NOT_AUDIT, styleHistory);
        }
        return result;
    }

    @Override
    public void getNotAuditStyleVersion() {
        redisTemplate.delete(KeywordUtil.STYLE_NOT_AUDIT);
        List<StyleHistoryDO> styleHistoryDOs  = styleHistoryDAO.getNotAuditStyleVersion();
        if(styleHistoryDOs!=null&&styleHistoryDOs.size()>0) {
            List<StyleHistory> styleHistories = styleHistoryDOs.stream().map(styleHistoryDO -> {
                StyleHistory styleHistory = styleHistoryDO.toModel();
                if(styleHistoryDO.getStyleId() != null) {
                    styleHistory.setStyle(getStyleById(styleHistory.getStyle().getId()));
                }
                return styleHistory;
            }).collect(Collectors.toList());
            redisTemplate.opsForList().leftPushAll(KeywordUtil.STYLE_NOT_AUDIT, styleHistories);
        }
    }

    @Override
    public StyleHistory dispatchMission() {
        StyleHistory styleHistory = (StyleHistory) redisTemplate.opsForList().rightPop(KeywordUtil.STYLE_NOT_AUDIT);
        if(styleHistory != null) {
            System.out.println(LocalDateTime.now()+"，"+styleHistory.getId()+"审核任务被派发");
            delayedQueue.addQueue(styleHistory, 15, TimeUnit.MINUTES, RHQueueListener.class.getName());
        }
        return styleHistory;
    }

    @Override
    public int updateStyleVersion(StyleHistoryDO styleHistoryDO) {
        return styleHistoryDAO.updateStyleVersion(styleHistoryDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean auditStyle(Map<String, Object> dataMap) {
        try {
            StyleDO styleData = JSONObject.parseObject((String) dataMap.get("styleData"), StyleDO.class);
            StyleHistoryDO historyData = JSONObject.parseObject((String) dataMap.get("historyData"), StyleHistoryDO.class);

            if(historyData.getIsNew() == 1) {
                styleDAO.addStyle(styleData);
                historyData.setStyleId(styleData.getId());
            } else if(historyData.getIsNew() == 0) {
                styleDAO.updateStyle(styleData);
            }
            styleHistoryDAO.updateStyleVersion(historyData);
            lockUtil.unlock(styleData.getEnName(), LockUtil.MODIFYING_STYLE);
            auditCountUtil.newAudit(AuditorUtil.STYLES, historyData.getId(), historyData.getAuditorId());

            int pt;
            if(historyData.getIsNew() == 1) {
                pt = 10;
            } else {
                pt = 5;
            }
            contributeUtil.contributePointAdd(historyData.getUserId(), pt);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<StyleHistory> getAllStyleVersion(String styleName, Long userId) {
        List<StyleHistoryDO> styleHistoryDOs = styleHistoryDAO.getAllStyleVersion(styleName);
        List<StyleHistory> styleHistories = styleHistoryDOs.stream().map(styleHistoryDO -> {
            StyleHistory styleHistory = styleHistoryDO.toModel();
            styleHistory.setThanks(thankUtil.getThankCount(styleHistory.getId(), ThankUtil.STYLE_AREA));
            styleHistory.setThank(thankUtil.getIsThank(styleHistory.getId(), userId, ThankUtil.STYLE_AREA));
            return styleHistory;
        }).collect(Collectors.toList());

        return styleHistories;
    }

    @Override
    public void styleContributeThanksCount() {
        Map thankMap = thankUtil.contributeThankCount(ThankUtil.STYLE_AREA);
        if(thankMap!=null&&thankMap.size()>0) {
            styleHistoryDAO.updateStyleThanks(thankMap);
        }
    }

    @Component
    public class RHQueueListener implements DelayedQueueListener<StyleHistory> {
        Logger logger = LoggerFactory.getLogger(RHQueueListener.class);

        @Autowired
        private StyleHistoryDAO styleHistoryDAO;

        @Autowired
        private RedisTemplate redisTemplate;

        @Override
        public void invoke(StyleHistory styleHistory) {
            int isPass = styleHistoryDAO.isAudit(styleHistory.getId());
            //该条目未被审核
            if(isPass==0) {
                logger.info("我执行完毕了，条目未被审核");
                redisTemplate.opsForList().leftPush(KeywordUtil.STYLE_NOT_AUDIT, styleHistory);
                //该条目已经被审核
            } else {
                if(isPass==1||isPass==2) {
                    logger.info("我执行完毕了，条目已被审核");
                } else {
                    logger.info("我执行完毕了，但是出现错误");
                    redisTemplate.opsForList().leftPush(KeywordUtil.STYLE_NOT_AUDIT, styleHistory);
                }
            }
        }
    }
}
