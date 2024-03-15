package com.zjz.server.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KaptchaConfig {

    /**
     * 创建并配置KaptchaProducer实例，用于生成验证码。
     *
     * @return Producer 配置好的KaptchaProducer实例，可以用于生成验证码图片。
     */
    @Bean
    public Producer kaptchaProducer() {
        // 初始化Kaptcha配置属性
        Properties properties = new Properties();
        properties.setProperty("kaptcha.image.width", "100"); // 验证码图片宽度
        properties.setProperty("kaptcha.image.height", "40"); // 验证码图片高度
        properties.setProperty("kaptcha.textproducer.font.size", "32"); // 字体大小
        properties.setProperty("kaptcha.textproducer.font.color", "0,0,0"); // 字体颜色
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"); // 可选字符集
        properties.setProperty("kaptcha.textproducer.char.length", "4"); // 验证码长度
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise"); // 关闭噪声

        // 创建Kaptcha实例并配置
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        Config config = new Config(properties);
        kaptcha.setConfig(config);
        return kaptcha;
    }


}