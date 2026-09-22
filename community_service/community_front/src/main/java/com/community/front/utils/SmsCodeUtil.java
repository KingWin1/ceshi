package com.community.front.utils;

import com.community.common.exception.BusinessException;
import com.community.common.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * 手机验证码工具类
 * 当前为Mock实现：Redis存码 + 60秒限流 + 开发环境回显，
 * 上线时仅需替换 doSend 方法内部为真实短信SDK调用，对外接口结构不变
 */
@Component
public class SmsCodeUtil {

    /** 验证码有效期（秒） */
    private static final long CODE_EXPIRE_SECONDS = 300;

    /** 同一手机号重发间隔（秒） */
    private static final long SEND_LIMIT_SECONDS = 60;

    /** 验证码键前缀 */
    private static final String CODE_KEY_PREFIX = "sms:code:";

    /** 发送限流键前缀 */
    private static final String LIMIT_KEY_PREFIX = "sms:limit:";

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 获取手机验证码（Mock实现）
     * @param phone 手机号
     * @return 验证码（开发环境回显给前端，上线后返回null）
     */
    public String sendCode(String phone) {
        // 1. 60秒限流：同一手机号一分钟内只能发一次
        String limitKey = LIMIT_KEY_PREFIX + phone;
        if (redisUtil.hasKey(limitKey)) {
            throw new BusinessException("发送过于频繁，请稍后再试");
        }

        // 2. 生成6位随机验证码并存入Redis（5分钟有效）
        String code = randomCode();
        redisUtil.set(CODE_KEY_PREFIX + phone, code, CODE_EXPIRE_SECONDS);
        redisUtil.set(limitKey, "1", SEND_LIMIT_SECONDS);

        // 3. 发送短信（当前Mock，仅打印日志）
        doSend(phone, code);
        return code;
    }

    /**
     * 校验手机号验证码
     * @param phone 手机号
     * @param code 用户输入的验证码
     * @return 是否验证成功
     */
    public boolean verifyCode(String phone, String code) {
        if (phone == null || phone.isEmpty() || code == null || code.isEmpty()) {
            return false;
        }
        String key = CODE_KEY_PREFIX + phone;
        Object cached = redisUtil.get(key);
        if (cached == null) {
            return false;
        }
        if (!cached.toString().equals(code)) {
            return false;
        }
        // 验证成功后立即删除，防止验证码重复使用
        redisUtil.delete(key);
        return true;
    }

    /**
     * 真实发送短信（预留结构）
     * TODO 上线前替换为腾讯云/阿里云短信SDK调用，方法签名保持不变
     */
    private void doSend(String phone, String code) {
        System.out.println("[Mock短信] 向 " + phone + " 发送验证码：" + code);
    }

    /**
     * 生成6位随机数字验证码
     */
    private String randomCode() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
