package com.zjz.server.controller;

import com.zjz.server.entity.ResponseResult;
import com.zjz.server.entity.vo.UserVo;
import com.zjz.server.service.UserService;
import com.zjz.server.utils.UserHolder;
import io.swagger.v3.oas.annotations.Parameter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/upload")
    public ResponseResult<?> upload(@RequestParam("file") MultipartFile file) {
        Map<String, Object> map = new HashMap<>();

        // 校验图片
        if (file == null) {
            map.put("errorMsg", "您还没有选择图片！");
            return ResponseResult.fail(map);
        }
        String fileName = file.getOriginalFilename();
        if (StringUtils.isBlank(fileName)) {
            map.put("errorMsg", "文件的格式不正确");
            return ResponseResult.fail(map);
        }
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            map.put("errorMsg", "文件的格式不正确");
            return ResponseResult.fail(map);
        }
        if (file.getSize() > 1024 * 1024 * 5) {
            map.put("errorMsg", "图片大小不能超过5M！");
            return ResponseResult.fail(map);
        }
        String url = "https://telegraph-image-17t.pages.dev/file/38e4e2b5afa7d999fbee5.jpg";
        UserVo user = UserHolder.getUser();
        int rows = userService.updateHeader(user.getId(),url);
        map.put("url", url);
        return ResponseResult.okResult(map);
    }
}
