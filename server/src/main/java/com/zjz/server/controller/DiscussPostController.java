package com.zjz.server.controller;

import cn.hutool.core.bean.BeanUtil;
import com.zjz.server.entity.*;
import com.zjz.server.entity.vo.Page;
import com.zjz.server.entity.vo.UserVo;
import com.zjz.server.enums.AppHttpCodeEnum;
import com.zjz.server.service.CommentService;
import com.zjz.server.service.DiscussPostService;
import com.zjz.server.service.UserService;
import com.zjz.server.utils.RedisCache;
import com.zjz.server.utils.RedisKeyUtil;
import com.zjz.server.utils.UserHolder;
import lombok.AllArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.zjz.server.utils.CommunityConstant.*;

@RestController
@RequestMapping("/discuss")
public class DiscussPostController {

    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;
    @PostMapping("/add")
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
    @GetMapping("/detail/{id}")
    public ResponseResult<?> getDiscussPost(@PathVariable("id") int id) {
        return discussPostService.selectDiscussPostById(id);
    }

    @GetMapping("detail/{discussPostId}/comment")
    public ResponseResult<?> getDiscussPostComment(@PathVariable("discussPostId") int discussPostId,
                                                   @RequestParam("current") int current,
                                                   @RequestParam("limit") int limit) {
        Page page = new Page();
        page.setCurrent(current);
        page.setLimit(limit);
        page.setRows(discussPostService.findDiscussPostById(discussPostId).getCommentCount());

        List<Comment> comments = commentService.selectCommentByEntity(ENTITY_TYPE_POST, discussPostId, page.getOffset(), page.getLimit());
        if(comments == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.FAIL, "获取评论失败");
        List<Map<String,Object>> commentVoList =  comments.stream().map(comment -> {
            Map<String,Object> commentVo = new HashMap<>();
            commentVo.put("comment", comment);
            User userById = userService.findUserById(comment.getUserId());
            UserVo userVo = BeanUtil.copyProperties(userById, UserVo.class);
            commentVo.put("user", userVo);
            List<Comment> commentReplyList = commentService.selectCommentByEntity(ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
            List<Map<String,Object>> replyVoList = commentReplyList.stream().map(reply -> {
                Map<String,Object> replyVo = new HashMap<>();
                replyVo.put("reply", reply);
                User replyUser = userService.findUserById(reply.getUserId());
                UserVo replyUserVo = BeanUtil.copyProperties(replyUser, UserVo.class);
                replyVo.put("user", replyUserVo);

                User targetUser = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                UserVo targetUserVo = new UserVo();
                if(targetUser != null){
                     BeanUtils.copyProperties(targetUser, targetUserVo);
                }
                replyVo.put("target", targetUserVo);
                return replyVo;
            }).collect(Collectors.toList());
            commentVo.put("replies", replyVoList);

            // 回复数量
            int replyCount = commentService.selectCountByEntity(ENTITY_TYPE_COMMENT, comment.getId());
            commentVo.put("count", replyCount);
            return commentVo;
        }).collect(Collectors.toList());


        return ResponseResult.okResult(commentVoList);
    }

}
