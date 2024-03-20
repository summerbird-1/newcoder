package com.zjz.server;

import com.zjz.server.dao.MessageMapper;
import com.zjz.server.entity.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class MessageMapperTest {
    @Autowired
    private MessageMapper messageMapper;
    @Test
    void messageMapperTest() {
        int i = messageMapper.selectUnreadLetterRows(111, "111_145");
        System.out.println(i);
        int i1 = messageMapper.selectUnreadLetterRows(111, "111_133");
        System.out.println(i1);
        int i2 = messageMapper.selectUnreadLetterRows(111, "111_112");
        System.out.println(i2);
        int i3 = messageMapper.selectUnreadLetterRows(111, "111_111");
        System.out.println(i3);
        int i4 = messageMapper.selectUnreadLetterRows(111, "111_138");
        System.out.println(i4);

    }
}