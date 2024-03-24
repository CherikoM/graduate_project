package com.example.project.util.RedisOperationUtils;

import com.example.project.util.KeywordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class LockUtil {

    @Autowired
    private RedisTemplate redisTemplate;

    public static String MODIFYING_RELEASE = "modifying-release";

    public static String MODIFYING_ARTIST = "modifying-artist";

    public static String MODIFYING_LABEL = "modifying-label";

    public static String MODIFYING_STYLE = "modifying-style";

    public <T> boolean isLock (T id, String area) {
        Long uid = (Long) redisTemplate.opsForValue().get(area+id);
        if(uid!=null) {
            return true;
        } else {
            return false;
        }
    }

    public <T> void lock(T id, Long userId, String area) {
        redisTemplate.opsForValue().set(area+id, userId);
    }

    public <T> void unlock(T id, String area) {
        redisTemplate.delete(area+id);
    }
}
