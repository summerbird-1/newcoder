package com.zjz.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.zjz.server.entity.ResponseResult;
import com.zjz.server.entity.User;
import com.zjz.server.entity.dto.LoginDto;
import com.zjz.server.entity.vo.UserVo;
import com.zjz.server.service.LoginService;
import com.zjz.server.service.UserService;
import com.zjz.server.utils.CommunityUtil;
import com.zjz.server.utils.JwtTokenUtil;
import com.zjz.server.utils.RedisCache;
import com.zjz.server.utils.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.zjz.server.utils.CommunityConstant.LOGIN_USER_KEY;

@Service
public class LoginServiceImpl implements LoginService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    /**
     * 用户登录接口。
     *
     * @param loginDto 包含登录信息的DTO（数据传输对象），包括用户名、密码和是否记住我。
     * @param kaptchaOwner 验证码的所有者标识，通常为用户输入验证码时的相关标识，用于匹配验证码。
     * @return 返回登录结果，如果登录失败，返回包含错误信息的Map；如果登录成功，返回包含成功标识的Map。
     */
    @Override
    public ResponseResult<?> login(LoginDto loginDto, String kaptchaOwner) {
        Map<String,Object> map = new HashMap<>();
        // 验证码为空的校验
        if(StringUtils.isBlank(kaptchaOwner)){
            map.put("codeMsg","验证码错误");
            return ResponseResult.okResult(map);
        }
        // 从Redis获取验证码的关键字和值
        String kaptchaKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        String kaptcha = stringRedisTemplate.opsForValue().get(kaptchaKey);
//        String kaptcha = redisCache.getCacheObject(kaptchaKey);
        // 验证码错误的校验
        if(StringUtils.isBlank(kaptcha) || StringUtils.isBlank(loginDto.getCode()) || !loginDto.getCode().equalsIgnoreCase(kaptcha)){
            map.put("codeMsg","验证码错误");
            return ResponseResult.okResult(map);
        }
        // 执行登录逻辑
        map = Login(loginDto.getUsername(),loginDto.getPassword(),loginDto.getRememberMe());
        return ResponseResult.okResult(map);
    }

    /**
     * 用户登录功能实现。
     *
     * @param username 用户名。
     * @param password 密码。
     * @param rememberMe 是否记住我。
     * @return 返回一个包含登录结果的Map，其中包含token和user信息，或者错误信息。
     */
    private Map<String, Object> Login(String username, String password, Boolean rememberMe) {
          Map<String, Object> map = new HashMap<>();
          // 检查用户名是否为空
          if(StringUtils.isBlank(username)){
              map.put("usernameMsg","用户名不能为空");
              return map;
          }
          // 检查密码是否为空
          if(StringUtils.isBlank(password)){
              map.put("passwordMsg","密码不能为空");
              return map;
          }
          // 根据用户名查找用户
          User user = userService.findUserByUserName(username);
          if(user == null){
              map.put("usernameMsg","用户不存在");
              return map;
          }
          // 检查用户是否激活
          if(user.getStatus() == 0){
              map.put("usernameMsg","用户未激活");
              return map;
          }
          // 对密码进行加密处理并与数据库中的密码进行比较
          password = CommunityUtil.md5(password+user.getSalt());
          if(!password.equals(user.getPassword())){
              map.put("passwordMsg","密码错误");
              return map;
          }
          // 登录成功，生成token并返回用户信息
        String token = jwtTokenUtil.generateAccessToken(username);
        UserVo userVo = BeanUtil.copyProperties(user, UserVo.class);
        Map<String, Object> userMap = BeanUtil.beanToMap(userVo, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));
        // 7.3.存储,这样可以使用token获取用户信息，该信息以hashmap形式返还
        String tokenKey = LOGIN_USER_KEY + token;
        stringRedisTemplate.opsForHash().putAll(tokenKey, userMap);
        // 7.4.设置token有效期
        if(rememberMe)
            stringRedisTemplate.expire(tokenKey, 3600*24, TimeUnit.SECONDS);
        else
            stringRedisTemplate.expire(tokenKey, 3600, TimeUnit.SECONDS);

        map.put("token",token);
        map.put("user",userVo);
        return map;
    }
}
