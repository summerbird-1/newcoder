package com.zjz.server.service.impl;


import com.zjz.server.dao.UserMapper;
import com.zjz.server.entity.ResponseResult;
import com.zjz.server.entity.User;
import com.zjz.server.enums.AppHttpCodeEnum;
import com.zjz.server.service.UserService;
import com.zjz.server.utils.CommunityUtil;
import com.zjz.server.utils.MailClient;
import com.zjz.server.utils.RedisCache;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private MailClient mailClient;
    @Autowired
    private RedisCache redisCache;

    @Override
    public User findUserById(int id) {
        return userMapper.findUserById(id);
    }

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Override
    public ResponseResult<?> registry(User user) {
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.USERNAME_NOT_NULL, "用户名不能为空！");
        }
        if (StringUtils.isBlank(user.getPassword())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PASSWORD_NOT_NULL, "密码不能为空！");
        }
        if (StringUtils.isBlank(user.getEmail())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.EMAIL_NOT_NULL, "邮箱不能为空！");
        }
        User userByName = userMapper.findUserByName(user.getUsername());
        if (userByName != null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.USERNAME_EXIST, "用户名已存在！");
        }
        User userByMail = userMapper.findUserByMail(user.getEmail());
        if (userByMail != null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.EMAIL_EXIST, "邮箱已存在！");
        }
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword().concat(user.getSalt())));
        user.setType((byte) 0);
        user.setStatus((byte) 0);
        String activationCode = CommunityUtil.generateUUID();
        user.setActivationCode(activationCode);
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(LocalDateTime.now());
        userMapper.insertUser(user);
        // 发送激活邮件
        //TODO: 需要判断邮件是否发送成功 1：邮箱不存在，2：邮件发送失败
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult<?> activate(int userId, String activationCode) {
        User userById = userMapper.findUserById(userId);
        if (userById == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR, "用户不存在！");
        }
        if (userById.getStatus() == 1)
            return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR, "用户已激活！");
        else if (Objects.equals(userById.getActivationCode(), activationCode)) {
            userMapper.updateStatus(userId, 1);
            return ResponseResult.okResult();
        }
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR, "激活失败！");
    }

    @Override
    public User findUserByUserName(String username) {
        return userMapper.findUserByName(username);
    }

    @Override
    public int updateHeader(Integer id, String url) {
        //@TODO 更新缓存
        return userMapper.updateHeader(id,url);
    }


}
