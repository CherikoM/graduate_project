package com.example.project.util.RedisOperationUtils;

import com.example.project.dao.UserDAO;
import com.example.project.dataobject.UserDO;
import com.example.project.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FollowUtil {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private RedisTemplate redisTemplate;

    //粉丝列表
    private static String USER_FOLLOWERS = "-user-followers";

    //关注列表
    private static String USER_FOLLOWS = "-user-follows";

    /**
     * 粉丝是否已关注爱豆
     * @param targetId 爱豆
     * @param followerId 粉丝
     * @return
     */
    public <T> boolean isFollow(T targetId, Long followerId) {
        //粉丝的关注列表
        String key = followerId+USER_FOLLOWS;
        Object value = redisTemplate.opsForHash().get(key, targetId);
        return value != null;
    }

    /**
     * 爱豆是否已回关粉丝
     * @param targetId 爱豆
     * @param followerId 粉丝
     * @return
     */
    public <T> boolean isFollowFlip(T targetId, Long followerId) {
        //爱豆的关注列表
        String key = targetId+USER_FOLLOWS;
        Object value = redisTemplate.opsForHash().get(key, followerId);
        return value != null;
    }

    public <T> boolean isFriend(T followId, Long followerId) {
        return isFollowFlip(followId, followerId) && isFollow(followId, followerId);
    }

    /**
     * 进行关注操作
     * @param targetId 爱豆
     * @param follower 粉丝
     */
    public void follow(Long targetId, User follower) {
        //爱豆
        UserDO followUser = userDAO.getUserById(targetId);

        //爱豆的粉丝列表
        String followKey = targetId+USER_FOLLOWERS;
        //爱豆的粉丝列表中添加粉丝
        redisTemplate.opsForHash().put(followKey, follower.getId(), follower.getName());

        //粉丝的关注列表
        String followerKey = follower.getId()+USER_FOLLOWS;
        //粉丝的关注列表中添加爱豆
        redisTemplate.opsForHash().put(followerKey, targetId, followUser.getName());
    }

    /**
     * 进行取关操作
     * @param targetId 爱豆
     * @param follower 粉丝
     */
    public void unFollow(Long targetId, User follower) {
        //爱豆
        UserDO followUser = userDAO.getUserById(targetId);

        //爱豆的粉丝列表
        String followKey = targetId+USER_FOLLOWERS;
        //爱豆的粉丝列表中添加粉丝
        redisTemplate.opsForHash().delete(followKey, follower.getId(), follower.getName());

        //粉丝的关注列表
        String followerKey = follower.getId()+USER_FOLLOWS;
        //粉丝的关注列表中添加爱豆
        redisTemplate.opsForHash().delete(followerKey, targetId, followUser.getName());
    }

    /**
     * 查询关注列表
     * @param userId
     * @return
     */
    public Map myFollows(Long userId) {
        String key = userId+USER_FOLLOWS;
        Map map = redisTemplate.opsForHash().entries(key);

        return map;
    }

    /**
     * 查询粉丝列表
     * @param userId
     * @return
     */
    public Map myFollowers(Long userId) {
        String key = userId+USER_FOLLOWERS;
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 查询关注总数
     * @param userId
     * @return
     */
    public Long myFollowCount(Long userId) {
        String key = userId+USER_FOLLOWS;
        return redisTemplate.opsForHash().size(key);
    }

    /**
     * 查询粉丝总数
     * @param userId
     * @return
     */
    public Long myFollowerCount(Long userId) {
        String key = userId+USER_FOLLOWERS;
        return redisTemplate.opsForHash().size(key);
    }

    public Set<Long> getAllUser() {
        Set<String> followUser = redisTemplate.keys("*"+USER_FOLLOWS);
        Set<String> fanUser = redisTemplate.keys("*"+USER_FOLLOWERS);
        Set<Long> follow = followUser.stream().map(u-> Long.valueOf(u.substring(0, u.indexOf("-")))).collect(Collectors.toSet());
        Set<Long> fan = fanUser.stream().map(u-> Long.valueOf(u.substring(0, u.indexOf("-")))).collect(Collectors.toSet());
        Set<Long> result = new HashSet<>();
        result.addAll(follow);
        result.addAll(fan);
        return result;
    }

//    public Set<Long> getUserFollow
}
