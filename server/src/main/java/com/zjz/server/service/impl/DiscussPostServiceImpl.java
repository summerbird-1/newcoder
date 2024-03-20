package com.zjz.server.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.zjz.server.dao.DiscussPostMapper;
import com.zjz.server.entity.DiscussPost;
import com.zjz.server.entity.ResponseResult;
import com.zjz.server.entity.User;
import com.zjz.server.entity.vo.UserVo;
import com.zjz.server.service.DiscussPostService;
import com.zjz.server.service.UserService;
import com.zjz.server.utils.SensitiveFilter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.internal.LoadingCache;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DiscussPostServiceImpl implements DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private UserService userService;
    // 帖子列表的缓存

    @Override
    public List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit, int orderMode) {

        return discussPostMapper.selectDiscussPosts(userId, offset, limit, orderMode);
    }

    @Override
    public int selectDiscussPostRows(int userId) {
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    @Override
    public int addDiscussPost(DiscussPost discussPost) {
        if(discussPost == null)
            throw new IllegalArgumentException("DiscussPost is null");
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));
        discussPost.setTitle(sensitiveFilter.filter(discussPost.getTitle()));
        discussPost.setContent(sensitiveFilter.filter(discussPost.getContent()));

        return discussPostMapper.InsertDiscussPost(discussPost);
    }

    @Override
    public ResponseResult<?> selectDiscussPostById(int id) {
        Map<String,Object> map = new HashMap<>();
        DiscussPost discussPost = discussPostMapper.selectDiscussPostById(id);
        if(discussPost == null)
            return ResponseResult.errorResult(501,"帖子不存在");
        map.put("discussPost",discussPost);
        User userById = userService.findUserById(discussPost.getUserId());
        UserVo userVo = BeanUtil.copyProperties(userById, UserVo.class);
        map.put("user",userVo);
        return ResponseResult.okResult(map);
    }

    @Override
    public DiscussPost findDiscussPostById(int discussPostId) {
       return discussPostMapper.selectDiscussPostById(discussPostId);
    }

    @Override
    public int updateCommentCount(int id, int commentCount) {
        return discussPostMapper.updateCommentCount(id, commentCount);
    }
}
