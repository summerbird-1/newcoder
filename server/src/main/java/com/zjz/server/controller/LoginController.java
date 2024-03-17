package com.zjz.server.controller;

import com.google.code.kaptcha.Producer;
import com.zjz.server.entity.ResponseResult;
import com.zjz.server.entity.User;
import com.zjz.server.entity.dto.LoginDto;
import com.zjz.server.service.LoginService;
import com.zjz.server.service.UserService;
import com.zjz.server.utils.CommunityUtil;
import com.zjz.server.utils.RedisCache;
import com.zjz.server.utils.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
public class LoginController {
    @Autowired
    private UserService userService;
    @Autowired
    private Producer kaptchaProducer;
   @Autowired
   private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private LoginService loginService;
    @Value("${server.servlet.session.cookie.path}")
    private String cookiePath;

    @PostMapping("/register")
    public ResponseResult<?> registry(@RequestBody User user)
    {
        return userService.registry(user);
    }
    /**
     * 获取验证码的控制器方法。
     * 该方法通过生成一个验证码文本和对应的图像，将验证码以图像形式返回给客户端。
     * 同时，将验证码文本通过cookie的形式存储在客户端，以供后续表单提交时进行验证。
     *
     * @param response 用于设置响应内容类型和输出验证码图像的HttpServletResponse对象。
     */
    @GetMapping(path = "/kaptcha", produces = MediaType.IMAGE_PNG_VALUE)
    public void getKaptcha(HttpServletResponse response){
        // 生成验证码文本
        String text = kaptchaProducer.createText();
        // 根据验证码文本生成对应的图像
        BufferedImage image = kaptchaProducer.createImage(text);

        // 生成一个唯一的验证码拥有者标识，并设置为cookie，以便在客户端进行验证
        String kaptchaOwner = CommunityUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
        cookie.setMaxAge(60); // 设置cookie的过期时间为60秒
        cookie.setPath(cookiePath); // 设置cookie的作用路径
        response.addCookie(cookie); // 将cookie添加到响应中

        //将验证码存入redis
        String kaptchaKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        stringRedisTemplate.opsForValue().set(kaptchaKey,text,60,TimeUnit.SECONDS);
        // 设置响应的内容类型为PNG图像
        response.setContentType("image/png");
        try{
            // 将验证码图像输出到响应流中
            ServletOutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        }catch (IOException e){
            // 记录响应验证码失败的日志
            log.error("响应验证码失败：{}",e.getMessage());
        }
    }
    /**
     * 处理用户登录请求。
     *
     * @param loginDto 包含登录所需信息的数据传输对象，例如用户名和密码。
     * @param kaptchaOwner 可选参数，用于验证用户所输入的验证码是否正确。如果验证码验证是登录流程的一部分，该值应当从用户的Cookie中获取。
     * @return 返回一个响应结果，该结果可以包含登录是否成功的信息，以及登录成功后的用户信息等。
     */
    @PostMapping("/login")
    public ResponseResult<?> login(@RequestBody LoginDto loginDto,
                                   @CookieValue(value = "kaptchaOwner",required = false)String kaptchaOwner)
    {
        // 将登录请求转发给登录服务处理
        return loginService.login(loginDto, kaptchaOwner);
    }

    @PutMapping("/logout")
    public ResponseResult<?> logout()
    {
        // 将登录请求转发给登录服务处理
        return ResponseResult.okResult();
    }
    @PutMapping("/activate/{userId}/{activationCode}")
    public ResponseResult<?> activate(@PathVariable("userId") int userId, @PathVariable("activationCode") String activationCode)
    {
        return userService.activate(userId,activationCode);
    }
}
