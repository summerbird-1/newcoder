package com.zjz.server.service.impl;

import com.zjz.server.service.LikeService;
import com.zjz.server.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl implements LikeService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Override
    public boolean like(int userId, int entityType, int entityId, int entityUserId) {
       final boolean[] isMember = {false};
       redisTemplate.execute(new SessionCallback<Object>() {
           @Override
           public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
               String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
               String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);
               isMember[0] =  Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(entityLikeKey,userId));

               operations.multi();
               if(isMember[0]){
                   redisTemplate.opsForSet().remove(entityLikeKey,userId);
                   redisTemplate.opsForValue().decrement(userLikeKey);
               }else{
                   redisTemplate.opsForSet().add(entityLikeKey,userId);
                   redisTemplate.opsForValue().increment(userLikeKey);
               }

               operations.exec();
               return null;
           }
       });
       return isMember[0];
    }

    @Override
    public long getLikeCount(int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    @Override
    public int isLiked(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey,userId) ? 1 : 0;
    }

    @Override
    public int findUserLikeCount(int userId) {
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count  = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count;
    }

}