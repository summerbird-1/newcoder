package com.zjz.server.service.impl;

import com.zjz.server.service.FollowService;
import com.zjz.server.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class FollowServiceImpl implements FollowService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Override
    public void follow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback<Object>() {
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

                operations.multi();

                operations.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis());
                operations.opsForZSet().add(followerKey, userId, System.currentTimeMillis());

                return operations.exec();
            }
        });
    }

    @Override
    public void unfollow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback<Object>() {
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

                operations.multi();

                operations.opsForZSet().remove(followeeKey, entityId);
                operations.opsForZSet().remove(followerKey, userId);

                return operations.exec();
            }
        });
    }

    @Override
    public boolean followed(Integer id, int entityType, int entityId) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(id, entityType);
        return redisTemplate.opsForZSet().score(followeeKey, entityId) != null;
    }

    @Override
    public int findFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        Long count = redisTemplate.opsForZSet().zCard(followerKey);
        return count == null ? 0 : count.intValue();
    }

    /**
     * 查询指定用户ID和实体类型的关注者数量。
     *
     * @param userId 用户ID，表示要查询关注者数量的用户。
     * @param entityType 实体类型，用于区分不同类型的实体，例如用户、博客等。
     * @return 返回关注者数量，如果没有找到对应的关注者，则返回0。
     */
    @Override
    public int findFolloweeCount(int userId, int entityType) {
        // 生成Redis中存储关注者列表的键
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        // 从Redis中获取关注者列表的数量
        Long count = redisTemplate.opsForZSet().zCard(followeeKey);
        // 如果未找到对应的关注者列表，则返回0，否则返回关注者数量
        return count == null ? 0 : count.intValue();
    }
}
