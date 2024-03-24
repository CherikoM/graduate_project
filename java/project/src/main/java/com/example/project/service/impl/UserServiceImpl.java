package com.example.project.service.impl;

import com.alibaba.fastjson2.JSON;
import com.example.project.common.Paging;
import com.example.project.common.Result;
import com.example.project.dao.*;
import com.example.project.dataobject.*;
import com.example.project.model.User;
import com.example.project.service.UserService;
import com.example.project.util.JwtUtil;
import com.example.project.util.RedisOperationUtils.AuditorUtil;
import com.example.project.util.RedisOperationUtils.ContributeUtil;
import com.example.project.util.RedisOperationUtils.FollowUtil;
import com.example.project.util.RedisOperationUtils.ThankUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private AuditorDAO auditorDAO;

    @Autowired
    private ReleaseHistoryDAO releaseHistoryDAO;

    @Autowired
    private ArtistHistoryDAO artistHistoryDAO;

    @Autowired
    private LabelHistoryDAO labelHistoryDAO;

    @Autowired
    private StyleHistoryDAO styleHistoryDAO;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ThankUtil thankUtil;

    @Autowired
    private FollowUtil followUtil;

    @Autowired
    private AuditorUtil auditorUtil;

    @Autowired
    private ContributeUtil contributeUtil;

    @Override
    public Result register(UserDO userDO, HttpSession session) {
        userDO.setPassword(makeMd5Password(userDO.getPassword()));

        if (userDAO.getUserByMailAdd(userDO.getMail()) != null) {
            return Result.error("邮箱已被注册！");
        } else {
            int addNum = userDAO.addUser(userDO);
            if(addNum == 0) {
                return Result.error("注册失败");
            }

            Map<String, String> tokens;

            try {
                tokens = JwtUtil.makeUserTokens(userDO);
                return Result.success(tokens);
            } catch (Exception e) {
                return Result.error("注册失败," + e.getMessage());
            }
        }
    }

    @Override
    public Result login(UserDO userDO, HttpSession session) {
        UserDO u = userDAO.getUserByMailAdd(userDO.getMail());
        if (u.getPassword().equals(makeMd5Password(userDO.getPassword()))) {
            Map tokens;
            AuditorDO auditorDO = auditorDAO.getAuditorByUserId(u.getId());
            try {
                tokens = JwtUtil.makeUserTokens(u);
                if(auditorDO!=null&&auditorDO.getCanUse()==1) {
                    auditorUtil.addAuditor(u.getId(), auditorDO.getId());
                    tokens.put("auditorId", auditorDO.getId());
                }
                return Result.success(tokens);
            } catch (Exception e) {
                return Result.error("登录失败," + e.getMessage());
            }
        }
        return Result.error("密码有误！");
    }

    @Override
    public Result logout(Long userId) {
        redisTemplate.delete("validate-test"+userId);
//        auditorUtil.removeAuditor(userId);
        return Result.success("Logout success!");
    }

    private String makeMd5Password(String password) {
        String saltPwd = "114514" + password;
        String md5Pwd = DigestUtils.md5Hex(saltPwd).toUpperCase();
        return md5Pwd;
    }

    @Override
    public User getUserById(Long targetId, Long userId) {
        UserDO userDO = userDAO.getUserById(targetId);
        if(userDO != null) {
            User user = userDO.toModel();
            user.setFollow(followUtil.myFollowCount(targetId));
            user.setFan(followUtil.myFollowerCount(targetId));
            if(targetId != userId) {
                user.setIsFollow(followUtil.isFollow(targetId, userId));
                user.setIsFollower(followUtil.isFollowFlip(targetId, userId));
                user.setIsFriend(isFriend(targetId, userId));
            }
            user.setContributePoint(contributeUtil.getContributePoint(userId));
            return user;
        } else {
            return null;
        }
    }

    @Override
    public Result auditVerify(Long auditorId, Long userId) {
        if(auditorUtil.isAuditor(userId, auditorId)) {
            return Result.success("Auditor verify success!");
        } else {
            return Result.error("Auditor verify failed!");
        }
    }

    @Override
    public int updateUser(UserDO userDO) {
        return userDAO.updateUser(userDO);
    }

    @Override
    public Result getAuditData(Long auditorId) {
        Map resultMap = new HashMap<>();

        Map aCount = new HashMap<>();
        aCount.put("releases", auditorUtil.getAreaAuditCount(AuditorUtil.RELEASES));
        aCount.put("artists", auditorUtil.getAreaAuditCount(AuditorUtil.ARTISTS));
        aCount.put("labels", auditorUtil.getAreaAuditCount(AuditorUtil.LABELS));
        aCount.put("styles", auditorUtil.getAreaAuditCount(AuditorUtil.STYLES));
        resultMap.put("aCount", aCount);

        Map auCount = new HashMap<>();
        auCount.put("releases", auditorUtil.getAreaYouAuditCount(AuditorUtil.RELEASES, auditorId));
        auCount.put("artists", auditorUtil.getAreaYouAuditCount(AuditorUtil.ARTISTS, auditorId));
        auCount.put("labels", auditorUtil.getAreaYouAuditCount(AuditorUtil.LABELS, auditorId));
        auCount.put("styles", auditorUtil.getAreaYouAuditCount(AuditorUtil.STYLES, auditorId));
        resultMap.put("auCount", auCount);

        Map tCount = new HashMap<>();
        tCount.put("releases", auditorUtil.getAreaAuditCountToday(AuditorUtil.RELEASES));
        tCount.put("artists", auditorUtil.getAreaAuditCountToday(AuditorUtil.ARTISTS));
        tCount.put("labels", auditorUtil.getAreaAuditCountToday(AuditorUtil.LABELS));
        tCount.put("styles", auditorUtil.getAreaAuditCountToday(AuditorUtil.STYLES));
        resultMap.put("tCount", tCount);

        Map tuCount = new HashMap<>();
        tuCount.put("releases", auditorUtil.getAreaYouAuditCountToday(AuditorUtil.RELEASES, auditorId));
        tuCount.put("artists", auditorUtil.getAreaYouAuditCountToday(AuditorUtil.ARTISTS, auditorId));
        tuCount.put("labels", auditorUtil.getAreaYouAuditCountToday(AuditorUtil.LABELS, auditorId));
        tuCount.put("styles", auditorUtil.getAreaYouAuditCountToday(AuditorUtil.STYLES, auditorId));
        resultMap.put("tuCount", tuCount);

        return Result.success(resultMap);
    }

    @Override
    public Paging getUserContribute(Long contributorId, Long userId, int pageNum, int pageSize) {
        Page<Map> page = PageHelper.startPage(pageNum, pageSize).doSelectPage(()-> userDAO.getUserContribute(contributorId));
        List<Map> contributeMapList = page.getResult();
        List<Map> result = contributeMapList.stream().map(map-> {
            map.put("size", thankUtil.getThankCount(map.get("id"), (String) map.get("area")));
            if(!userId.equals(contributorId)) {
                map.put("isThank", thankUtil.getIsThank(map.get("id"), userId, (String) map.get("area")));
            }
            return map;
        }).collect(Collectors.toList());
        return new Paging<>(page.getPageNum(), page.getPageSize(), page.getPages(), page.getTotal(), result);
    }

    @Override
    public long doThankContribute(String area, Long historyId, Long userId) {
        return thankUtil.doThank(historyId, userId, area);
    }

    @Override
    public long undoThankContribute(String area, Long historyId, Long userId) {
        return thankUtil.undoThank(historyId, userId, area);
    }

    @Override
    public Map getPost(String area, Long historyId, Long userId) {
        Map map = new HashMap<>();
        map.put("size", thankUtil.getThankCount(historyId, area));
        map.put("isThank", thankUtil.getIsThank(historyId, userId, area));
        return map;
    }

    @Override
    public <T> boolean isFollow(T followId, Long userId) {
        return followUtil.isFollow(followId, userId);
    }

    @Override
    public void follow(Long followId, User user) {
        followUtil.follow(followId, user);
    }

    @Override
    public void unFollow(Long followId, User user) {
        followUtil.unFollow(followId, user);
    }

    @Override
    public Paging<List> myFollows(Long userId, int pageNum, int pageSize) {
        return getFriendList(userId, followUtil.myFollows(userId), pageNum, pageSize);
    }

    @Override
    public Paging<List> myFollowers(Long userId, int pageNum, int pageSize) {
        return getFriendList(userId, followUtil.myFollowers(userId), pageNum, pageSize);
    }

    @Override
    public <T> boolean isFriend(T followId, Long followerId) {
        return followUtil.isFriend(followId, followerId);
    }

    @Override
    public Long myFollowCount(Long userId) {
        return followUtil.myFollowCount(userId);
    }

    @Override
    public Long myFollowerCount(Long userId) {
        return followUtil.myFollowerCount(userId);
    }

    @Override
    public void addContributePoint() {
        List<Long> ids = contributeUtil.getAllUsers();
        Map userMap = ids.stream().collect(Collectors.toMap(u-> u, u-> contributeUtil.getContributePoint(u)));
        userDAO.updateContributePoint(userMap);
    }

    @Override
    public void addFollowFan() {
        Set<Long> ids = followUtil.getAllUser();
        Map followMap = ids.stream().collect(Collectors.toMap(id-> id, id-> {
            Map m = followUtil.myFollows(id);
            if(m==null) {
                return null;
            } else {
                Set follows = m.keySet();
                return JSON.toJSONString(follows);
            }
        }));
        Map fanMap = ids.stream().collect(Collectors.toMap(id-> id, id-> {
            Map m = followUtil.myFollowers(id);
            if(m==null) {
                return null;
            } else {
                Set follows = m.keySet();
                return JSON.toJSONString(follows);
            }
        }));
        userDAO.updateFollow(followMap);
        userDAO.updateFan(fanMap);
    }

    @Override
    public void addAuditPoint() {
        List<Long> ids = auditorUtil.getAllAuditors();

        Map auditorMap = new HashMap<>();
        auditorMap.put("releases", ids.stream().collect(Collectors.toMap(a->a, a-> {
            Long res = auditorUtil.getAreaYouAuditCount(AuditorUtil.RELEASES, a);
            if(res == null) return 0;
            else return res;
        })));
        auditorMap.put("artists", ids.stream().collect(Collectors.toMap(a->a, a-> {
            Long res = auditorUtil.getAreaYouAuditCount(AuditorUtil.ARTISTS, a);
            if(res == null) return 0;
            else return res;
        })));
        auditorMap.put("labels", ids.stream().collect(Collectors.toMap(a->a, a-> {
            Long res = auditorUtil.getAreaYouAuditCount(AuditorUtil.LABELS, a);
            if(res == null) return 0;
            else return res;
        })));
        auditorMap.put("styles", ids.stream().collect(Collectors.toMap(a->a, a-> {
            Long res = auditorUtil.getAreaYouAuditCount(AuditorUtil.STYLES, a);
            if(res == null) return 0;
            else return res;
        })));

        auditorDAO.updateAuditPoint(auditorMap);
    }

    @Override
    public void clearTodayAudit() {
        auditorUtil.clearToday();
    }

    private Paging<List> getFriendList(Long userId, Map<Object, Object> map, int pageNum, int pageSize) {
        List allIds = Arrays.asList(map.keySet().toArray());
        int from = (pageNum-1)*pageSize;
        int to = pageNum*pageSize;
        int totalPage = allIds.size()/pageSize;
        if(allIds.size()%pageSize>0) {
            totalPage++;
        }
        if(from>allIds.size() || allIds.size() == 0) {
            return new Paging<>(pageNum, pageSize, totalPage, allIds.size(), null);
        }
        if(to>allIds.size()) {
            to = allIds.size();
        }
        List ids = allIds.subList(from, to);
        List<Map> m = userDAO.getFriendList(ids);
        List result = m.stream().map(map1 -> {
            map1.put("isFollow", isFollow(map1.get("id"), userId));
            map1.put("friend", isFriend(map1.get("id"), userId));
            map1.put("contributePoint", contributeUtil.getContributePoint((Long) map1.get("id")));
            return map1;
        }).collect(Collectors.toList());

        return new Paging<>(pageNum, pageSize, totalPage, allIds.size(), result);
    }


}
