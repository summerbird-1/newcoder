package com.zjz.server.service;

import com.zjz.server.entity.Message;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MessageService {
    List<Message> selectConversations(int userId,int offset,int limit);
    //查询会话数量
    int selectConversationRows(int userId);
    //查询某个会话包含消息列表
    List<Message> selectLetters(String conversationId, int offset,int limit);
    //查询某个会话包含的私信数量
    int selectLetterRows(String conversationId);
    //查询未读私信数量
    int selectUnreadLetterRows(int userId,String conversationId);

    int readMessages(List<Integer> unreadLetterIds);

    int addMessage(Message message);
}
