package com.zjz.server.service;

import com.zjz.server.entity.ResponseResult;
import com.zjz.server.entity.dto.LoginDto;

public interface LoginService {
    ResponseResult<?> login(LoginDto loginDto, String kaptchaOwner);
}
