package com.zjz.server.interceptor;

import cn.hutool.core.bean.BeanUtil;
import com.zjz.server.entity.User;
import com.zjz.server.entity.vo.UserVo;
import com.zjz.server.utils.CommunityConstant;
import com.zjz.server.utils.CookieUtil;
import com.zjz.server.utils.UserHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RefreshTokenIntercepter implements HandlerInterceptor {
    private StringRedisTemplate stringRedisTemplate;
    public RefreshTokenIntercepter(StringRedisTemplate stringRedisTemplate){
        this.stringRedisTemplate = stringRedisTemplate;
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从cookie中获取token
        String token = CookieUtil.getValue(request, "token");
        //基于token获取用户信息
        if(StringUtils.isBlank(token)){
            //token为空,放行
            return true;
        }

        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(CommunityConstant.LOGIN_USER_KEY + token.substring(9));
        //判断用户是否存在
        if(userMap.isEmpty()){
            //用户不存在,放行
            return true;
        }
        //将查询到的Hash数据转换为UserDTO
        UserVo userVo = BeanUtil.fillBeanWithMap(userMap, new UserVo(), false);
        //用户存在，保存用户信息到ThreadLocal
        UserHolder.saveUser(userVo);
        //刷新token有效期
        stringRedisTemplate.expire(CommunityConstant.LOGIN_USER_KEY + token, 60*24, TimeUnit.MINUTES);
        //放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}