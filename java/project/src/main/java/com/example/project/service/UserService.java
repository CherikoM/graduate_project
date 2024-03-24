package com.example.project.service;

import com.example.project.common.Paging;
import com.example.project.common.Result;
import com.example.project.dataobject.UserDO;
import com.example.project.model.User;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface UserService {

    Result register(UserDO userDO, HttpSession httpSession);

    Result login(UserDO userDO, HttpSession httpSession);

    Result logout(Long userId);

    User getUserById(Long targetId, Long userId);

    int updateUser(UserDO userDO);

    Result auditVerify(Long auditorId, Long userId);

    Result getAuditData(Long auditorId);

    Paging getUserContribute(Long contributorId, Long userId, int pageNum, int pageSize);

    long doThankContribute(String area, Long historyId, Long userId);

    long undoThankContribute(String area, Long historyId, Long userId);

    Map getPost(String area, Long historyId, Long userId);

    <T> boolean isFollow(T followId, Long userId);

    void follow(Long followId, User user);

    void unFollow(Long followId, User user);

    Paging<List> myFollows(Long userId, int pageNum, int pageSize);

    Paging<List> myFollowers(Long userId, int pageNum, int pageSize);

    <T> boolean isFriend(T followId, Long followerId);

    Long myFollowCount(Long userId);

    Long myFollowerCount(Long userId);

    void addContributePoint();

    void addFollowFan();

    void addAuditPoint();

    void clearTodayAudit();
}
