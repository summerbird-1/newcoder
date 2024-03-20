package com.zjz.server.controller;

import com.zjz.server.entity.ResponseResult;
import com.zjz.server.entity.dto.FollowDto;
import com.zjz.server.entity.vo.Page;
import com.zjz.server.service.FollowService;
import com.zjz.server.service.UserService;
import com.zjz.server.utils.CommunityConstant;
import com.zjz.server.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class FollowController {
    @Autowired
    private FollowService followService;
    @Autowired
    private UserService userService;

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
    @GetMapping("/followee/{userId}")
    public ResponseResult<?> findFollowees(@PathVariable("userId") int userId, @RequestParam("current") int current, @RequestParam("limit") int limit) {
        if(userService.findUserById(userId) == null)
            return ResponseResult.errorResult(404, "用户不存在");
        Page page = new Page();
        page.setCurrent(current);
        page.setLimit(limit);
        page.setRows(followService.findFolloweeCount(userId, CommunityConstant.ENTITY_TYPE_USER));
        return ResponseResult.okResult(followService.findFollowees(userId, page.getOffset(), page.getLimit()));
    }
    @GetMapping("/follower/{userId}")
    public ResponseResult<?> findFollowers(@PathVariable("userId") int userId, @RequestParam("current") int current, @RequestParam("limit") int limit) {
        if(userService.findUserById(userId) == null)
            return ResponseResult.errorResult(404, "用户不存在");
        Page page = new Page();
        page.setCurrent(current);
        page.setLimit(limit);
        page.setRows(followService.findFollowerCount(CommunityConstant.ENTITY_TYPE_USER, userId));
        return ResponseResult.okResult(followService.findFollowers(userId, page.getOffset(), page.getLimit()));
    }
}
