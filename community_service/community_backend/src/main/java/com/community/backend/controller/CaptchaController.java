package com.community.backend.controller;

import com.community.backend.utils.CaptchaUtil;
import com.community.common.utils.RedisUtil;
import com.community.common.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 验证码接口
 */
@RestController
@RequestMapping("/api/captcha")
@Api(tags = "验证码管理")
public class CaptchaController {

    @Autowired
    private RedisUtil redisUtil;

    @GetMapping("/generate")
    @ApiOperation("生成图形验证码")
    public Result<Map<String, Object>> generateCaptcha() {
        // 生成验证码
        CaptchaUtil.CaptchaInfo captchaInfo = CaptchaUtil.generate();

        // 将验证码文本存入Redis，key为captcha:uuid，过期时间300秒
        String redisKey = "captcha:" + captchaInfo.getUuid();
        redisUtil.set(redisKey, captchaInfo.getCode(), captchaInfo.getExpireSeconds());

        // 返回uuid和base64图片（不返回验证码文本）
        Map<String, Object> data = new HashMap<>();
        data.put("uuid", captchaInfo.getUuid());
        data.put("image", captchaInfo.getImage());

        return Result.success(data);
    }
}
