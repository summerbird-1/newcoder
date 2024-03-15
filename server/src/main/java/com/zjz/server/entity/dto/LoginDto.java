package com.zjz.server.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginDto {
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
    private String code;
    private Boolean rememberMe;


}

