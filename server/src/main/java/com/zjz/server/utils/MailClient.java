package com.zjz.server.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
@Slf4j
public class MailClient {

    private final JavaMailSender mailSender;

    public MailClient(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Value("${spring.mail.username}")
    private String from;

    /**
     * 发送邮件的函数。
     *
     * @param to 接收邮件的邮箱地址。
     * @param subject 邮件的主题。
     * @param content 邮件的内容。
     * 注意：此函数不返回任何值，也不会抛出任何检查型异常，但是可能会在捕获到MessagingException异常时记录错误日志。
     */
    public void sendMail(String to, String subject, String content) {
        try {
            // 创建MimeMessage邮件对象
            MimeMessage message = mailSender.createMimeMessage();
            // 使用MimeMessageHelper助手来设置邮件信息
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(from); // 设置发件人
            helper.setTo(to); // 设置收件人
            helper.setSubject(subject); // 设置邮件主题
            helper.setText(content, true); // 设置邮件正文，true表示使用HTML格式
            // 发送邮件
            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            // 记录发送邮件失败的错误日志
            log.error("发送邮件失败：{}", e.getMessage());
        }
    }
}