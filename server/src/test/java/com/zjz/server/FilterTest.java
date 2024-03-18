package com.zjz.server;

import com.zjz.server.utils.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class FilterTest {
@Autowired
private SensitiveFilter sensitiveFilter;
    @Test
    public void test() {
        String text = "这里可以吸*毒，嫖娼";
        String filter = sensitiveFilter.filter(text);
        System.out.println(filter);
        System.out.println("test");
    }
}
