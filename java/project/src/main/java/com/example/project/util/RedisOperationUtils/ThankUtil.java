package com.example.project.util.RedisOperationUtils;

import com.alibaba.fastjson2.JSONArray;
import com.example.project.dao.ArtistHistoryDAO;
import com.example.project.dao.LabelHistoryDAO;
import com.example.project.dao.ReleaseHistoryDAO;
import com.example.project.dao.StyleHistoryDAO;
import com.example.project.util.KeywordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class ThankUtil {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ReleaseHistoryDAO releaseHistoryDAO;

    @Autowired
    private ArtistHistoryDAO artistHistoryDAO;

    @Autowired
    private LabelHistoryDAO labelHistoryDAO;

    @Autowired
    private StyleHistoryDAO styleHistoryDAO;

    public static String HISTORY_THANK = "-history-thank";

    public static String RELEASE_AREA = "releases";

    public static String ARTIST_AREA = "artists";

    public static String LABEL_AREA = "labels";

    public static String STYLE_AREA = "styles";

    public <T> long getThankCount(T areaId, String area) {
        String key = areaId +"-" + area + HISTORY_THANK;
        return redisTemplate.opsForSet().size(key);
    }

    public <T> boolean getIsThank(T areaId, Long userId, String area) {
        String key = areaId +"-" + area + HISTORY_THANK;
        return redisTemplate.opsForSet().isMember(key, userId);
    }

    public Map contributeThankCount(String area) {
        String suffix = "-"+area+HISTORY_THANK;
        Set<String> thanks = redisTemplate.keys("*"+suffix);
        Map thankMap = new HashMap<>();
        for(String key: thanks) {
            Long id = Long.valueOf(key.substring(0, key.indexOf("-")));
            Set a = redisTemplate.opsForSet().members(key);
            if(a!=null) {
                String jsonStr = JSONArray.toJSONString(a.toArray());
                thankMap.put(id, jsonStr);
            } else {
                thankMap.put(id, null);
            }
        }
        return thankMap;
    }

    public long doThank(Long areaId, Long userId, String area) {
        return redisTemplate.opsForSet().add(areaId+"-"+area+ HISTORY_THANK, userId);
    }

    public long undoThank(Long areaId, Long userId, String area) {
        String key = areaId+"-"+area+HISTORY_THANK;
        long result = redisTemplate.opsForSet().remove(key, userId);
        long rest = redisTemplate.opsForSet().size(key);
        if(rest == 0) {
            Map map = new HashMap<>();
            map.put(areaId, null);
            if(area.equals(RELEASE_AREA)) {
                releaseHistoryDAO.updateReleaseThanks(map);
            } else if(area.equals("artists")) {
                artistHistoryDAO.updateArtistThanks(map);
            } else if(area.equals("labels")) {
                labelHistoryDAO.updateLabelThanks(map);
            } else if (area.equals("styles")) {
                styleHistoryDAO.updateStyleThanks(map);
            }
        }
        return result;
    }
}
