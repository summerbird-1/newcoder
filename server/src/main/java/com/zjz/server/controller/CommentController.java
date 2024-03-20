package com.zjz.server.controller;

import com.zjz.server.entity.Comment;
import com.zjz.server.entity.DiscussPost;
import com.zjz.server.entity.ResponseResult;
import com.zjz.server.service.CommentService;
import com.zjz.server.service.DiscussPostService;
import com.zjz.server.utils.CommunityConstant;
import com.zjz.server.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/comment")
public class CommentController {
@Autowired
private CommentService commentService;
@Autowired
private DiscussPostService discussPostService;
    @PostMapping("/add")
    public ResponseResult<?> addComment(@RequestBody Comment comment) {
        Integer UserId = UserHolder.getUser().getId();
        comment.setUserId(UserId);
        comment.setCreateTime(LocalDateTime.now());
        comment.setStatus((byte) 0);
        commentService.insertComment(comment);

        if(comment.getEntityType() == CommunityConstant.ENTITY_TYPE_POST){
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());

        }
        return ResponseResult.okResult();
    }
}
