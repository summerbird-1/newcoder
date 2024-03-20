package com.zjz.server;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class RedisTests {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Test
    void testString() {
        redisTemplate.opsForValue().set("id",1);
        System.out.println(redisTemplate.opsForValue().get("id"));
        redisTemplate.opsForValue().increment("id");
        System.out.println(redisTemplate.opsForValue().get("id"));
        redisTemplate.opsForValue().decrement("id");
        System.out.println(redisTemplate.opsForValue().get("id"));
    }
    @Test
    void testList() {
        redisTemplate.opsForList().leftPush("list","1");
        redisTemplate.opsForList().leftPush("list","2");
        System.out.println(redisTemplate.opsForList().size("list"));
        System.out.println(redisTemplate.opsForList().index("list",0));
        System.out.println(redisTemplate.opsForList().range("list",0,1));
        redisTemplate.opsForList().leftPush("list","3");
        System.out.println(redisTemplate.opsForList().range("list",0,2));
        redisTemplate.opsForList().leftPop("list");
    }
}
