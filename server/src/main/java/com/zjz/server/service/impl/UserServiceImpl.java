package com.zjz.server.service.impl;

import com.zjz.server.dao.UserMapper;
import com.zjz.server.entity.User;
import com.zjz.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public User findUserById(int id) {
        return userMapper.findUserById(id);
    }
}
