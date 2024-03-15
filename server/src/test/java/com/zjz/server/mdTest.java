package com.zjz.server;

import com.zjz.server.utils.CommunityUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class mdTest {
   @Test
    public void test(){
        String s = "123456";
       String s1 = CommunityUtil.md5(s + "980f6");
       System.out.println(s1);
   }
}
