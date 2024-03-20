package com.zjz.server.dao;

import com.zjz.server.entity.Message;
import org.apache.commons.lang3.Conversion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageMapper {
       //查询当前用户的会话列表，针对每个会话返回一条最新的私信
    List<Message> selectConversations(@Param("userId") int userId, @Param("offset") int offset, @Param("limit") int limit);
    //查询会话数量
    int selectConversationRows(int userId);
    //查询某个会话包含消息列表
    List<Message> selectLetters(@Param("conversationId") String conversationId, @Param("offset") int offset, @Param("limit") int limit);
    //查询某个会话包含的私信数量
    int selectLetterRows(String conversationId);
    //查询未读私信数量
    int selectUnreadLetterRows(@Param("userId") int userId,@Param("conversationId")String conversationId);

    int updateStatus(List<Integer> ids, @Param("status") byte status);

    int insertMessage(Message message);
}
