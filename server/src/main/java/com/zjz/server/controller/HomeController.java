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
    /**
     * 获取首页讨论帖子列表
     *
     * @param current 当前页码，默认为1
     * @param limit 每页显示数量，默认为10
     * @param orderMode 排序模式，默认为0（通常用于指定排序方式，如按时间、热度等）
     * @return 返回包含帖子列表的响应结果对象
     */
    @GetMapping("/index")
    public ResponseResult<?> getIndexPage( @RequestParam(value = "current", required = false, defaultValue = "1") int current,
                                           @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
                                           @RequestParam(value = "orderMode", required = false, defaultValue = "0") int orderMode){
        // 初始化分页信息
        Page page = new Page();
        page.setCurrent(current);
        page.setLimit(limit);
        page.setRows(discussPostService.selectDiscussPostRows(0)); // 查询总帖子数，用于分页

        // 查询当前页的帖子列表
        List<DiscussPost> discussPosts = discussPostService.selectDiscussPosts(0, page.getOffset(), page.getLimit(), orderMode);

        // 对查询到的帖子列表进行处理，封装成包含用户信息的Map列表
        List<Map<String, Object>> postList = discussPosts.parallelStream()
                .map(o -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("post", o);
                    User user = userService.findUserById(o.getUserId());
                    UserVo userVo = new UserVo();
                    BeanUtils.copyProperties(user, userVo); // 将用户信息从User模型复制到UserVo模型，用于前端展示
                    map.put("user", userVo);
                    return map;
                }).collect(Collectors.toList());

        // 返回处理后的帖子列表
        return ResponseResult.okResult(postList);
    }
    @GetMapping("/index/count")
    public ResponseResult<?> getIndexPageCount(){

        return ResponseResult.okResult(discussPostService.selectDiscussPostRows(0));
    }

}
