package com.zjz.server;

import com.zjz.server.utils.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@SpringBootTest
public class MailTest {
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;
    @Test
    public void testSendMail() {
        mailClient.sendMail("2301210532@stu.pku.edu.cn", "123456", "测试邮件");
    }
    @Test
    public void testSendMail2() {
        Context context = new Context();
        context.setVariable("username", "张三");
        String content = templateEngine.process("mail/activation", context);
        System.out.println(content);
        mailClient.sendMail("2301210532@stu.pku.edu.cn", "123456", content);
    }
}
