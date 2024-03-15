package com.zjz.server.service;

import com.zjz.server.entity.ResponseResult;
import com.zjz.server.entity.User;
import com.zjz.server.entity.dto.LoginDto;

import java.util.List;

public interface UserService {
    User findUserById(int id);

    ResponseResult<?> registry(User user);

    ResponseResult<?> activate(int userId,String activationCode);


    User findUserByUserName(String username);
}
