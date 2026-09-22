package com.community.front.service;

import com.community.front.dto.PasswordLoginDTO;
import com.community.front.dto.RegisterDTO;
import com.community.front.dto.SmsLoginDTO;
import com.community.front.dto.WxBindDTO;
import com.community.front.dto.WxLoginDTO;

import java.util.Map;

/**
 * 前台居民认证Service接口（登录/注册/找回密码/微信静默登录）
 */
public interface ResidentAuthService {

    /**
     * 获取手机验证码（Mock实现）
     * @param phone 手机号
     * @param type  验证码类型：1-注册 2-登录 3-找回密码
     * @return 含 devCode 的结果（开发环境回显）
     */
    Map<String, Object> sendSmsCode(String phone, Integer type);

    /**
     * 校验手机号+验证码
     * @param phone 手机号
     * @param code  验证码
     * @return 是否验证成功
     */
    boolean verifySmsCode(String phone, String code);

    /**
     * 手机号+验证码登录：校验通过后按手机号查询居民信息并签发token
     * @param smsLoginDTO 登录参数
     * @return resident + token
     */
    Map<String, Object> loginBySms(SmsLoginDTO smsLoginDTO);

    /**
     * 手机号+密码登录
     * @param passwordLoginDTO 登录参数
     * @return resident + token
     */
    Map<String, Object> loginByPassword(PasswordLoginDTO passwordLoginDTO);

    /**
     * 前台注册（校验手机号+验证码后新增居民，密码字段入库）
     * @param registerDTO 注册参数
     */
    void register(RegisterDTO registerDTO);

    /**
     * 找回密码：校验手机号+验证码后重置居民密码
     * @param registerDTO 重置参数
     */
    void resetPassword(RegisterDTO registerDTO);

    /**
     * 微信静默登录：code换openid，已绑定直接签发token，未绑定返回needBind
     * @param wxLoginDTO 登录参数
     * @return token/openid/needBind/resident
     */
    Map<String, Object> wxLogin(WxLoginDTO wxLoginDTO);

    /**
     * 微信绑定居民账号：手机号+验证码匹配 resident.phone 后回填绑定并签发token
     * @param wxBindDTO 绑定参数
     * @return resident + token
     */
    Map<String, Object> wxBind(WxBindDTO wxBindDTO);
}
