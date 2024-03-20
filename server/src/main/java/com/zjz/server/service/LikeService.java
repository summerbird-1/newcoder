package com.zjz.server.service;

public interface LikeService {

    public boolean like(int userId, int entityType, int entityId, int entityUserId);
    //查询某实体点赞数量
    public long getLikeCount(int entityType,int entityId);

    //查询某人对某实体是否点赞
    public int isLiked(int userId,int entityType,int entityId);

    public int findUserLikeCount(int userId);

}
