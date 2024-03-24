package com.example.project.util.RedisOperationUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AuditorUtil {

    @Autowired
    private RedisTemplate redisTemplate;

    //auditorId-area-audit-count
    private static String AUDIT_COUNT = "-audit-count";

    private static String AUDIT_COUNT_TODAY = "-audit-count-today";

    private static String AUDITOR = "auditor-";

    public static String RELEASES = "releases";

    public static String ARTISTS = "artists";

    public static String LABELS = "labels";

    public static String STYLES = "styles";

    public Long getAreaAuditCount(String area) {
        return redisTemplate.opsForZSet().size(area+AUDIT_COUNT);
    }

    public Long getAreaAuditCountToday(String area) {
        return redisTemplate.opsForZSet().size(area+AUDIT_COUNT_TODAY);
    }

    public Long getAreaYouAuditCount(String area, Long auditorId) {
        return Long.valueOf((redisTemplate.opsForZSet().rangeByScore(area+AUDIT_COUNT, auditorId, auditorId)).size());
    }

    public Long getAreaYouAuditCountToday(String area, Long auditorId) {
        return Long.valueOf((redisTemplate.opsForZSet().rangeByScore(area+AUDIT_COUNT_TODAY, auditorId, auditorId)).size());
    }

    public Long getAuditorPoint(Long auditorId) {
        return
                getAreaYouAuditCount(RELEASES, auditorId)+
                getAreaYouAuditCount(ARTISTS, auditorId)+
                getAreaYouAuditCount(LABELS, auditorId)+
                getAreaYouAuditCount(STYLES, auditorId);
    }

    public void newAudit(String area, Long historyId, Long auditorId) {
        redisTemplate.opsForZSet().add(area+AUDIT_COUNT, historyId, auditorId);
        redisTemplate.opsForZSet().add(area+AUDIT_COUNT_TODAY, historyId, auditorId);
    }

    public void addAuditor(Long userId, Long auditorId) {
        redisTemplate.opsForValue().set(AUDITOR+userId, auditorId);
    }

//    public void removeAuditor(Long userId) {
//        redisTemplate.delete(AUDITOR+userId);
//    }

    public boolean isAuditor(Long userId, Long auditorId) {
        Long a = (Long) redisTemplate.opsForValue().get(AUDITOR+userId);
        if(!Objects.equals(auditorId, a)) {
            return false;
        } else return true;
    }

    public List<Long> getAllAuditors() {
        Set<String> users = redisTemplate.keys(AUDITOR + "*");

        return users.stream().map(u-> (Long)redisTemplate.opsForValue().get(u)).collect(Collectors.toList());
    }

    public void clearToday() {
        redisTemplate.delete("*"+AUDIT_COUNT_TODAY);
    }
}
