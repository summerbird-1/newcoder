package com.zjz.server.controller;

import com.zjz.server.entity.ResponseResult;
import com.zjz.server.entity.User;
import com.zjz.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginController {
    @Autowired
    private UserService userService;
    @PostMapping("/register")
    public ResponseResult<?> registry(@RequestBody User user)
    {
        return userService.registry(user);
    }
    @PostMapping("/login")
    public ResponseResult<?> login(@RequestBody User user)
    {
        return userService.login(user);
    }
    @PutMapping("/activate/{userId}/{activationCode}")
    public ResponseResult<?> activate(@PathVariable("userId") int userId, @PathVariable("activationCode") String activationCode)
    {
        return userService.activate(userId,activationCode);
    }
}
