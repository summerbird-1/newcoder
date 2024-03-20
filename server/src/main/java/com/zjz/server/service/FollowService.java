package com.zjz.server.service;

public interface FollowService {
    void follow(int userId, int entityType, int entityId);
    void unfollow(int userId, int entityType, int entityId);

    boolean followed(Integer id, int entityType, int entityId);

    int findFollowerCount(int entityType, int entityId);

    int findFolloweeCount(int userId, int entityType);
}
