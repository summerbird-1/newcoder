package com.zjz.server.service.impl;

import com.zjz.server.dao.CommentMapper;
import com.zjz.server.entity.Comment;
import com.zjz.server.service.CommentService;
import com.zjz.server.service.DiscussPostService;
import com.zjz.server.utils.CommunityConstant;
import com.zjz.server.utils.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
@Service
public class CommentMapperImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostService discussPostService;
    @Override
    public List<Comment> selectCommentByEntity(int entityType, int entityId, int offset, int limit) {

        return commentMapper.selectCommentByEntity(entityType, entityId, offset, limit);
    }

    @Override
    public int selectCountByEntity(int entityType, int entityId) {
        return commentMapper.selectCountByEntity(entityType, entityId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    @Override
    public int insertComment(Comment comment) {
        if(comment == null)
            throw new IllegalArgumentException("参数不能为空");
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        int rows= commentMapper.insertComment(comment);

        if(comment.getEntityType() == CommunityConstant.ENTITY_TYPE_POST){
            int count = commentMapper.selectCountByEntity(CommunityConstant.ENTITY_TYPE_POST, comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(), count);
        }

        return 0;
    }
}
