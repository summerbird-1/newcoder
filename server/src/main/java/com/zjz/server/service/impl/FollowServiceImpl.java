package com.zjz.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.zjz.server.entity.User;
import com.zjz.server.entity.vo.UserVo;
import com.zjz.server.service.FollowService;
import com.zjz.server.service.UserService;
import com.zjz.server.utils.CommunityConstant;
import com.zjz.server.utils.RedisKeyUtil;
import com.zjz.server.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FollowServiceImpl implements FollowService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private UserService userService;
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

    @Override
    public List<Map<String, Object>> findFollowees(int userId, int offset, int limit) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, CommunityConstant.ENTITY_TYPE_USER);
        Set<Object> targetIds = redisTemplate.opsForZSet().range(followeeKey, offset, offset + limit);
        if(targetIds == null)
            return null;
        Integer loginId = UserHolder.getUser().getId();
        String userfolloweeKey = RedisKeyUtil.getFolloweeKey(loginId, CommunityConstant.ENTITY_TYPE_USER);
        return targetIds.stream().map(id -> {
            Map<String, Object> map = new HashMap<>();
            User userById = userService.findUserById((Integer) id);
            UserVo userVo = BeanUtil.copyProperties(userById, UserVo.class);
            map.put("user", userVo);
            Double score = redisTemplate.opsForZSet().score(followeeKey, id);
            if (score != null) {
                map.put("followTime", Instant.ofEpochMilli(score.longValue()).atZone(ZoneId.systemDefault())
                        .toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss")));
            }
            if (!loginId.equals(id)) {
                map.put("followed", redisTemplate.opsForZSet().score(userfolloweeKey, id) != null);
            }
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> findFollowers(int userId, int offset, int limit) {
        String followerKey = RedisKeyUtil.getFollowerKey(CommunityConstant.ENTITY_TYPE_USER, userId);
        Set<Object> targetIds = redisTemplate.opsForZSet().range(followerKey, offset, offset + limit);
        if(targetIds == null)
            return null;
        Integer loginId = UserHolder.getUser().getId();
        String userfollowerKey = RedisKeyUtil.getFollowerKey(CommunityConstant.ENTITY_TYPE_USER, loginId);
        return targetIds.stream().map(id -> {
            Map<String, Object> map = new HashMap<>();
            User userById = userService.findUserById((Integer) id);
            UserVo userVo = BeanUtil.copyProperties(userById, UserVo.class);
            map.put("user", userVo);
            Double score = redisTemplate.opsForZSet().score(followerKey, id);
            if (score != null) {
                map.put("followTime", Instant.ofEpochMilli(score.longValue()).atZone(ZoneId.systemDefault())
                        .toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss")));
            }
            if(!loginId.equals(id)){
                map.put("followed", redisTemplate.opsForZSet().score(userfollowerKey, id) != null);
            }
            return map;
        }).collect(Collectors.toList());
    }
}
