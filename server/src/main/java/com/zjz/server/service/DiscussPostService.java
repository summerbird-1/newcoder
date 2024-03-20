package com.zjz.server.service;

import com.zjz.server.entity.DiscussPost;
import com.zjz.server.entity.ResponseResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DiscussPostService {
    List<DiscussPost> selectDiscussPosts(int userId,int offset, int limit, int orderMode);
    int selectDiscussPostRows(@Param("userId")int userId);

    int addDiscussPost(DiscussPost discussPost);

    ResponseResult<?> selectDiscussPostById(int id);

    DiscussPost findDiscussPostById(int discussPostId);

    int updateCommentCount(int id, int commentCount);
}
