package com.zjz.server.entity;

import java.time.LocalDateTime;
import java.util.Date;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * (User)表实体类
 *
 * @author makejava
 * @since 2024-03-13 22:46:08
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class User  {

    private Integer id;


    private String username;

    private String password;

    private String salt;

    private String email;
    @Schema(name = "type", description = "用户类型，0-普通用户，1-超级管理员，2-版主")
    private Byte type;
    @Schema(name = "status", description = "用户状态，0-未激活，1-已激活")
    private Byte status;

    private String activationCode;

    private String headerUrl;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;



}
