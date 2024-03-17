package com.zjz.server;

import com.zjz.server.utils.RedisCache;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class redisTest {
    @Autowired
    private RedisCache redisCache;

    @Test
    public void test() {
        redisCache.setCacheObject("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdW1tZXIiLCJleHAiOjE3MTA2ODA1ODksImlhdCI6MTcxMDY0NDU4OX0.ycbuwLXU_nNgsn_Wc-mnmdg0qG554HjjEk0e7xSWBho", 1924665346,3600*24, TimeUnit.SECONDS);
        Integer cacheObject = redisCache.getCacheObject("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdW1tZXIiLCJleHAiOjE3MTA2ODA1ODksImlhdCI6MTcxMDY0NDU4OX0.ycbuwLXU_nNgsn_Wc-mnmdg0qG554HjjEk0e7xSWBho");
        System.out.println(cacheObject);
    }
}
