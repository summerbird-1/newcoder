package com.zjz.server.service;

import com.zjz.server.entity.Comment;

import java.util.List;

public interface CommentService {
    public List<Comment> selectCommentByEntity(int entityType, int entityId,int offset, int limit);
    public int selectCountByEntity(int entityType, int entityId);

    public int insertComment(Comment comment);
}
