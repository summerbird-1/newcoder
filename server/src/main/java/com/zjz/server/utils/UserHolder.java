package com.zjz.server.utils;


import com.zjz.server.entity.vo.UserVo;

public class UserHolder {
    private static final ThreadLocal<UserVo> tl = new ThreadLocal<>();

    public static void saveUser(UserVo user){
        tl.set(user);
    }

    public static UserVo getUser(){

        return tl.get();
    }

    public static void removeUser(){
        tl.remove();
    }
}
