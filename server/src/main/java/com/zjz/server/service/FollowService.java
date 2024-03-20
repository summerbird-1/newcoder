package com.zjz.server.service;

import com.zjz.server.entity.vo.UserVo;

import java.util.List;
import java.util.Map;

public interface FollowService {
    void follow(int userId, int entityType, int entityId);
    void unfollow(int userId, int entityType, int entityId);

    boolean followed(Integer id, int entityType, int entityId);

    int findFollowerCount(int entityType, int entityId);

    int findFolloweeCount(int userId, int entityType);

    // 查询某个用户关注的人
    List<Map<String, Object>> findFollowees(int userId, int offset, int limit);

    // 查询某用户的粉丝
    List<Map<String, Object>> findFollowers(int userId, int offset, int limit);
}
