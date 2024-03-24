package com.example.project.util.RedisOperationUtils;

import com.alibaba.fastjson2.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class CollectionUtil {

    @Autowired
    private RedisTemplate redisTemplate;

    private static String COLLECTION_LIST = "collection-list-";

    //一个collection的收藏者
    private static String COLLECTION_LIKER = "-collection-liker";

    //一个用户收藏的collection
    private static String LIKE_COLLECTION = "-like-collection";


    public boolean addItemToList(Long colId, String area, Long areaId) {
        Long size = getColSize(colId);
        if(colId > 500) {
            return false;
        }
        return redisTemplate.opsForZSet().add(COLLECTION_LIST+colId, area+"-"+areaId, System.currentTimeMillis());
    }

    public boolean deleteListItem(Long colId, String area, Long areaId) {
        Long res = redisTemplate.opsForZSet().remove(COLLECTION_LIST+colId, area+"-"+areaId);
        if(res > 0) {
            return true;
        } else {
            return false;
        }
    }

    public long getColSize(Long colId) {
        return redisTemplate.opsForZSet().size(COLLECTION_LIST+colId);
    }

    public boolean isInCol(Long colId, String area, Long areaId) {
        Double result = redisTemplate.opsForZSet().score(COLLECTION_LIST+colId, area+"-"+areaId);
        if(result == null) {
            return false;
        } else {
            return true;
        }
    }

    public List<Map> getList(Long colId, int pageNum, int pageSize) {
        String key = COLLECTION_LIST+colId;
        int from = (pageNum-1)*pageSize;
        int to = pageNum*pageSize-1;

        Set res = redisTemplate.opsForZSet().range(key, from, to);

        return (List<Map>) res.stream().map((Object item)-> {
            Map map = new HashMap<>();
            String i = (String) item;
            String area = i.substring(0, i.indexOf("-"));
            Long id = Long.valueOf(i.substring(i.indexOf("-")+1));

            map.put("area", area);
            map.put("id", id);

            return map;
        }).collect(Collectors.toList());
    }

    public List<Map> getList(Long colId) {
        String key = COLLECTION_LIST+colId;

        Set res = redisTemplate.opsForZSet().range(key, 0, -1);

        return (List<Map>) res.stream().map((Object item)-> {
            Map map = new HashMap<>();
            String i = (String) item;
            String area = i.substring(0, i.indexOf("-"));
            Long id = Long.valueOf(i.substring(i.indexOf("-")+1));

            map.put("area", area);
            map.put("id", id);

            return map;
        }).collect(Collectors.toList());
    }

    public boolean likeCollection(Long colId, Long userId) {
        boolean res1 = redisTemplate.opsForZSet().add(colId+COLLECTION_LIKER, userId, System.currentTimeMillis());
        boolean res2 = redisTemplate.opsForZSet().add(userId+LIKE_COLLECTION, colId, System.currentTimeMillis());

        return res1 && res2;
    }

    public boolean disLikeCollection(Long colId, Long userId) {
        long res1 = redisTemplate.opsForZSet().remove(colId+COLLECTION_LIKER, userId);
        long res2 = redisTemplate.opsForZSet().remove(userId+LIKE_COLLECTION, colId);

        return res1!=0 && res2!=0;
    }

    public List<Long> getUserLike(Long userId, int pageNum, int pageSize) {
        int from = (pageNum-1)*pageSize;
        int to = pageNum*pageSize-1;

        Set res = redisTemplate.opsForZSet().range(userId+LIKE_COLLECTION, from, to);
        List result = Arrays.asList(res.toArray());
        return (List<Long>) result.stream().map((Object obj)-> {
            Long o = (Long) obj;
            return o;
        }).collect(Collectors.toList());
    }

    public List getColLiker(Long colId, int pageNum, int pageSize) {
        int from = (pageNum-1)*pageSize;
        int to = pageNum*pageSize-1;

        Set res = redisTemplate.opsForZSet().range(colId+COLLECTION_LIKER, from, to);
        List result = Arrays.asList(res.toArray());
        return (List<Long>) result.stream().map((Object obj)-> {
            Long o = (Long) obj;
            return o;
        }).collect(Collectors.toList());
    }

    public long getUserLikeCount(Long userId) {
        return redisTemplate.opsForZSet().size(userId+LIKE_COLLECTION);
    }

    public long getColLikerCount(Long colId) {
        return redisTemplate.opsForZSet().size(colId+COLLECTION_LIKER);
    }

    /**
     * 用户是否喜欢某collection
     * @param colId
     * @param userId
     * @return
     */
    public boolean isLikeCol(Long colId, Long userId) {
        Double res1 = redisTemplate.opsForZSet().score(colId+COLLECTION_LIKER, userId);
        Double res2 = redisTemplate.opsForZSet().score(userId+LIKE_COLLECTION, colId);
        if(res1 == null || res2 == null) {
            return false;
        } else {
            return true;
        }
    }

    public void deleteCol(Long colId) {
        Set hasItem = redisTemplate.opsForZSet().range(colId+COLLECTION_LIST, 0, 1);
        if(hasItem != null && hasItem.size()>0) {
            redisTemplate.opsForZSet().remove(colId+COLLECTION_LIST);
        }

        Set hasLiker = redisTemplate.opsForZSet().range(colId+COLLECTION_LIKER, 0, 1);
        if(hasLiker != null && hasLiker.size()>0) {
            Long liker = redisTemplate.opsForZSet().remove(colId+COLLECTION_LIKER);

            if(liker >0) {
                Set<String> likes = redisTemplate.keys("*"+LIKE_COLLECTION);
                if(likes==null || likes.size()==0) {
                    return;
                }
                likes.forEach(l-> {
                    redisTemplate.opsForZSet().remove(l, colId);
                });
            }
        }
    }

    public List<Long> getAllCols() {
        Set<String> cols = redisTemplate.keys(COLLECTION_LIST + "*");
        List<Long> result = cols.stream().map(col-> {
            String idStr = col.substring(col.lastIndexOf("-")+1);
            return Long.valueOf(idStr);
        }).collect(Collectors.toList());

        return result;
    }

    public String getAllLikers(Long colId) {
        Set<Long> likers = redisTemplate.opsForZSet().range( colId + COLLECTION_LIKER, 0, -1);
        return JSON.toJSONString(likers);
    }
}
