package com.zjz.server.dao;

import com.zjz.server.entity.ResponseResult;
import com.zjz.server.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User findUserById(int id);
    int insertUser(User user);

    User findUserByName(String username);

    User findUserByMail(String email);

    int updateStatus(int userId, int i);
}
