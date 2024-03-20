package com.zjz.server.controller;

import com.zjz.server.entity.ResponseResult;
import com.zjz.server.entity.dto.FollowDto;
import com.zjz.server.service.FollowService;
import com.zjz.server.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class FollowController {
    @Autowired
    private FollowService followService;

    @PostMapping("/follow")
    public ResponseResult<?> follow(@RequestBody FollowDto followDto)
    {
        Integer id = UserHolder.getUser().getId();
        followService.follow(id, followDto.getEntityType(), followDto.getEntityId());
        return ResponseResult.okResult();
    }
    @PostMapping("/unfollow")
    public ResponseResult<?> unfollow(@RequestBody FollowDto followDto) {
        Integer id = UserHolder.getUser().getId();
        followService.unfollow(id, followDto.getEntityType(), followDto.getEntityId());
        return ResponseResult.okResult();
    }
    @GetMapping("/follow")
    public ResponseResult<?> followed(@RequestParam int entityType, @RequestParam int entityId) {
        Integer id = UserHolder.getUser().getId();
        return ResponseResult.okResult(followService.followed(id, entityType, entityId));
    }
    @GetMapping("/follower/count")
    public ResponseResult<?> findFollowerCount(@RequestParam int entityType, @RequestParam int entityId) {
        return ResponseResult.okResult(followService.findFollowerCount(entityType, entityId));
    }
    @GetMapping("followee/count")
    public ResponseResult<?> findFolloweeCount(@RequestParam int userId, @RequestParam int entityType) {
        return ResponseResult.okResult(followService.findFolloweeCount(userId, entityType));
    }
}
