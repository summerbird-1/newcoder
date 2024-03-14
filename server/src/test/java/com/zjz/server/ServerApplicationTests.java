package com.zjz.server;

import com.zjz.server.dao.DiscussPostMapper;
import com.zjz.server.entity.DiscussPost;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ServerApplicationTests {
     @Autowired
     private DiscussPostMapper discussPostMapper;
    @Test
    void contextLoads() {
    }


}
