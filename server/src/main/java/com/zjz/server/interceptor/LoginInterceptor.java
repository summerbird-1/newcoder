package com.zjz.server.interceptor;

import com.zjz.server.entity.User;
import com.zjz.server.service.UserService;
import com.zjz.server.utils.CookieUtil;
import com.zjz.server.utils.HostHolder;
import com.zjz.server.utils.JwtTokenUtil;
import com.zjz.server.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;
    /**
     * 在处理请求之前进行拦截，用于认证和授权。
     *
     * @param request  HttpServletRequest对象，代表客户端的HTTP请求
     * @param response HttpServletResponse对象，用于向客户端发送响应
     * @param handler  将要处理请求的处理器对象
     * @return boolean 返回true表示请求通过认证和授权，可以继续处理；返回false表示请求未通过，终止处理。
     * @throws Exception 抛出异常的情况主要处理认证和授权过程中的异常。
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从cookie中获取token
        String token = CookieUtil.getValue(request, "token");
        // 如果token不存在或为空，则返回false
        if (token == null || token.isEmpty()) {
            return false;
        }
        // 验证token的有效性
        if(!jwtTokenUtil.validateToken(token))
            return false;
        // 从Redis缓存中获取用户ID
        Integer userId = redisCache.getCacheObject(token);
        // 如果用户ID为空，则返回false
        if(userId == null)
            return false;
        // 从token中获取用户名
        String username = jwtTokenUtil.getUsernameFromToken(token);
        // 根据用户名查询用户信息
        User user = userService.findUserByUserName(username);
        // 如果用户不存在、用户状态不为1或用户ID与token中不一致，则返回false
        if(user == null || user.getStatus() != 1 || !Objects.equals(user.getId(), userId))
            return false;
        // 设置当前线程的用户对象
        hostHolder.setUser(user);
        return true;
    }


    /**
     * 在处理请求后执行的回调方法。
     * 该方法会检查当前用户是否登录，并将登录用户的信息添加到ModelAndView中，以便在视图中使用。
     *
     * @param request  当前HTTP请求
     * @param response 当前HTTP响应
     * @param handler  处理请求的处理器对象
     * @param modelAndView 渲染视图的ModelAndView对象，如果为null，则不进行任何操作。
     * @throws Exception 抛出所有异常
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser(); // 获取当前登录的用户
        if(user != null && modelAndView != null)
            modelAndView.addObject("loginUser", user); // 将登录用户信息添加到ModelAndView中
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
