package com.zjz.server.advice;


import com.zjz.server.entity.ResponseResult;
import com.zjz.server.utils.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler({Exception.class})
    public ResponseResult<Object> handleException(Exception e) {
        log.error("服务器发生异常：{}", e.getMessage());
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            log.error(stackTraceElement.toString());
        }
        return ResponseResult.errorResult(ResultCodeEnum.INTERNAL_SERVER_ERROR.getCode(),"服务器异常！");
    }
}
