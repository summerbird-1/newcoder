package com.zjz.server.config;

import com.zjz.server.interceptor.LoginIntercepter;
import com.zjz.server.interceptor.RefreshTokenIntercepter;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*") // 使用allowedOriginPatterns替代allowedOrigins
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("Authorization", "Cache-Control", "Content-Type")
                .allowCredentials(true)
                .maxAge(3600);
    }
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//    // 添加登录拦截器，并设置拦截所有请求
//        registry.addInterceptor(new LoginInterceptor())
//            .addPathPatterns("/**")
//            .excludePathPatterns("/index", "/login", "/register", "/kaptcha", "/index/count", "/activate/**","/logout");

    // 异常处理: 尽管在拦截器中不直接编码异常处理，但建议在拦截器逻辑中或被拦截的处理器方法中加入异常处理逻辑
    // 这里主要关注配置的优化，故不展开代码实现
//}
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginIntercepter())
                .excludePathPatterns(
                        "/index", "/login", "/register", "/kaptcha", "/index/count", "/activate/**","/logout"
                ).order(1);  //order越大，优先级越低
        registry.addInterceptor(new RefreshTokenIntercepter(stringRedisTemplate)).addPathPatterns("/**");

    }
}