package com.zjz.server.controller;

import com.zjz.server.entity.ResponseResult;
import com.zjz.server.entity.dto.LikeDto;
import com.zjz.server.service.LikeService;
import com.zjz.server.utils.RedisCache;
import com.zjz.server.utils.RedisKeyUtil;
import com.zjz.server.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class LikeController {

    @Autowired
    private LikeService likeService;
    @Autowired
    private RedisCache redisCache;

    @PostMapping("/like")
    public ResponseResult<?> like(@RequestBody LikeDto likeDto, @RequestParam("postId") int postId) {
        int userId = UserHolder.getUser().getId();
        boolean like = likeService.like(userId, likeDto.getEntityType(), likeDto.getEntityId(), likeDto.getEntityUserId());
        if(like)
        {
            String postScoreKey = RedisKeyUtil.getPostScoreKey();
            redisCache.setCacheSet(postScoreKey,postId);
        }
        return ResponseResult.okResult();
    }

    @GetMapping("/like/count")
    public ResponseResult<?> getLikeCount(@RequestParam("entityType") int entityType,
                                           @RequestParam("entityId") int entityId) {
        long count = likeService.getLikeCount(entityType, entityId);
        return ResponseResult.okResult(count);
    }
    @GetMapping("/like/status")
    public ResponseResult<?> isLiked(@RequestParam("entityType") int entityType,
                                      @RequestParam("entityId") int entityId) {
        int userId = UserHolder.getUser().getId();
        int status = likeService.isLiked(userId, entityType, entityId);
        return ResponseResult.okResult(status);
    }
}
