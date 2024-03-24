package com.example.project.util.RedisOperationUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ContributeUtil {

    @Autowired
    private RedisTemplate redisTemplate;

    private static String CONTRIBUTE_POINT = "-contribute-point";

    public void contributePointAdd(Long userId, int add) {
        Long nowPoint = (Long) redisTemplate.opsForValue().get(userId+CONTRIBUTE_POINT);
        Long newPoint = Long.valueOf(add);
        if(nowPoint!=null) {
            newPoint+=nowPoint;
        }
        redisTemplate.opsForValue().set(userId+CONTRIBUTE_POINT, newPoint);
    }

    public Long getContributePoint(Long userId) {
        Long res = (Long) redisTemplate.opsForValue().get(userId+CONTRIBUTE_POINT);
        if(res == null) {
            res = Long.valueOf(0);
        }
        return res;
    }

    public List<Long> getAllUsers() {
        Set<String> users = redisTemplate.keys("*"+CONTRIBUTE_POINT);
        return users.stream().map(user-> Long.valueOf(user.substring(0, user.indexOf("-")))).collect(Collectors.toList());
    }
}
