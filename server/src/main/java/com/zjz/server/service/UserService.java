package com.zjz.server.service;

import com.zjz.server.entity.ResponseResult;
import com.zjz.server.entity.User;

public interface UserService {
    User findUserById(int id);

    ResponseResult<?> registry(User user);

    ResponseResult<?> activate(int userId,String activationCode);


    User findUserByUserName(String username);

    int updateHeader(Integer id, String url);
}
