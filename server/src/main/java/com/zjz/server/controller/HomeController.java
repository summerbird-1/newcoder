package com.zjz.server.controller;

import com.zjz.server.entity.DiscussPost;
import com.zjz.server.entity.ResponseResult;
import com.zjz.server.entity.User;
import com.zjz.server.entity.vo.Page;
import com.zjz.server.entity.vo.UserVo;
import com.zjz.server.service.DiscussPostService;
import com.zjz.server.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/community")
public class HomeController {
    @Autowired
    private UserService userService;
    @Autowired
    private DiscussPostService discussPostService;

    @GetMapping("/test")
    public ResponseResult<?> test(){
        List<DiscussPost> discussPosts = discussPostService.selectDiscussPosts(0, 1, 10, 0);
        return ResponseResult.okResult(discussPosts);
    }
    @GetMapping("/index")
    public ResponseResult<?> getIndexPage( @RequestParam(value = "current", required = false, defaultValue = "1") int current,
                                           @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
                                           @RequestParam(value = "orderMode", required = false, defaultValue = "0") int orderMode){
        Page page = new Page();
        page.setCurrent(current);
        page.setLimit(limit);
        page.setRows(discussPostService.selectDiscussPostRows(0));
        List<DiscussPost> discussPosts = discussPostService.selectDiscussPosts(0, current, limit, orderMode);
        List<Map<String, Object>> postList = discussPosts.parallelStream()
                .map(o -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("post", o);
                    User user = userService.findUserById(o.getUserId());
                    UserVo userVo = new UserVo();
                    BeanUtils.copyProperties(user, userVo);
                    map.put("user", userVo);
                    return map;
                }).collect(Collectors.toList());
        return ResponseResult.okResult(postList);
    }
    @GetMapping("/index/count")
    public ResponseResult<?> getIndexPageCount(){

        return ResponseResult.okResult(discussPostService.selectDiscussPostRows(0));
    }

}
