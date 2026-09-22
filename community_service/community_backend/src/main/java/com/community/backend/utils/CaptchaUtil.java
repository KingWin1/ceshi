package com.community.backend.utils;

import com.wf.captcha.SpecCaptcha;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

/**
 * 图形验证码工具类（基于EasyCaptcha）
 */
public class CaptchaUtil {

    // 验证码宽度
    private static final int WIDTH = 130;
    // 验证码高度
    private static final int HEIGHT = 48;
    // 验证码字符数
    private static final int CHAR_COUNT = 4;
    // 验证码过期时间（秒）
    private static final int EXPIRE_SECONDS = 300;

    /**
     * 生成图形验证码
     * @return CaptchaInfo 包含uuid、base64图片、验证码文本
     */
    public static CaptchaInfo generate() {
        // 使用 SpecCaptcha，默认使用数字+字母混合
        SpecCaptcha captcha = new SpecCaptcha(WIDTH, HEIGHT, CHAR_COUNT);
        // 获取验证码文本（调用text()会触发生成）
        String text = captcha.text().toLowerCase();
        // 生成唯一标识
        String uuid = UUID.randomUUID().toString().replace("-", "");
        // 将图片转为Base64
        String base64 = toBase64(captcha);
        return new CaptchaInfo(uuid, base64, text, EXPIRE_SECONDS);
    }

    /**
     * 将验证码图片转为Base64字符串
     */
    private static String toBase64(SpecCaptcha captcha) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            captcha.out(out);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("验证码生成失败", e);
        }
    }

    /**
     * 验证码信息封装类
     */
    public static class CaptchaInfo {
        private String uuid;
        private String image;
        private String code;
        private int expireSeconds;

        public CaptchaInfo(String uuid, String image, String code, int expireSeconds) {
            this.uuid = uuid;
            this.image = image;
            this.code = code;
            this.expireSeconds = expireSeconds;
        }

        public String getUuid() { return uuid; }
        public String getImage() { return image; }
        public String getCode() { return code; }
        public int getExpireSeconds() { return expireSeconds; }
    }
}
