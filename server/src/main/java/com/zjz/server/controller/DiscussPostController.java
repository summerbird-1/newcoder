package com.zjz.server.controller;

import com.zjz.server.entity.DiscussPost;
import com.zjz.server.entity.Event;
import com.zjz.server.entity.ResponseResult;
import com.zjz.server.enums.AppHttpCodeEnum;
import com.zjz.server.service.DiscussPostService;
import com.zjz.server.utils.RedisCache;
import com.zjz.server.utils.RedisKeyUtil;
import com.zjz.server.utils.UserHolder;
import lombok.AllArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.zjz.server.utils.CommunityConstant.ENTITY_TYPE_POST;
import static com.zjz.server.utils.CommunityConstant.TOPIC_PUBLISH;

@RestController
public class DiscussPostController {

    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private RedisCache redisCache;
    @PostMapping("/discuss/add")
    public ResponseResult<?> addDiscussPost(@RequestBody DiscussPost discussPost) {
         discussPost.setCreateTime(LocalDateTime.now());
         discussPost.setStatus((byte) 0);
         discussPost.setCommentCount(0);
         discussPost.setType((byte) 0);
         discussPost.setScore(0.0);
         discussPost.setUserId(UserHolder.getUser().getId());

         int rows = discussPostService.addDiscussPost(discussPost);
         if (rows == 0)
             return ResponseResult.errorResult(AppHttpCodeEnum.FAIL, "添加失败");

        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(UserHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(discussPost.getId());
        //@TODO 送入生产者
        String postScoreKey = RedisKeyUtil.getPostScoreKey();
        redisCache.setCacheSet(postScoreKey, discussPost.getId());
        Map<String,Object> map = new HashMap<>();
        map.put("data",discussPost);
        map.put("success",true);
        return ResponseResult.okResult(map);
    }
}
