package com.zjz.server.interceptor;

import com.zjz.server.utils.UserHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginIntercepter implements HandlerInterceptor {
/**
 * 在请求处理之前执行的方法
 *
 * @param request  请求对象
 * @param response 响应对象
 * @param handler 处理器对象
 * @return 如果处理成功，返回true；否则返回false
 * @throws Exception 异常
 */
@Override
public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
     //判断ThreadLocal中是否有用户
     if(UserHolder.getUser() == null){
         response.setStatus(401);
         return false;
     }
    // 返回true
    return true;
}

}
