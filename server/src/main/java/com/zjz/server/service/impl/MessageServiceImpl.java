package com.zjz.server.service.impl;

import com.zjz.server.dao.MessageMapper;
import com.zjz.server.entity.Message;
import com.zjz.server.service.MessageService;
import com.zjz.server.utils.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Override
    public List<Message> selectConversations(int userId, int offset, int limit) {
        return messageMapper.selectConversations(userId, offset, limit);
    }

    @Override
    public int selectConversationRows(int userId) {
        return messageMapper.selectConversationRows(userId);
    }

    @Override
    public List<Message> selectLetters(String conversationId, int offset, int limit) {
        return messageMapper.selectLetters(conversationId, offset, limit);
    }

    @Override
    public int selectLetterRows(String conversationId) {
        return messageMapper.selectLetterRows(conversationId);
    }


    @Override
    public int selectUnreadLetterRows(int userId, String conversationId) {
        return messageMapper.selectUnreadLetterRows(userId, conversationId);
    }

    @Override
    public int readMessages(List<Integer> unreadLetterIds) {
        return messageMapper.updateStatus(unreadLetterIds, (byte) 1);
    }

    @Override
    public int addMessage(Message message) {
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insertMessage(message);
    }
}
