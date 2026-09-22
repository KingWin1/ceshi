package com.community.front.controller;

import com.community.common.utils.Result;
import com.community.front.dto.PasswordLoginDTO;
import com.community.front.dto.RegisterDTO;
import com.community.front.dto.SmsLoginDTO;
import com.community.front.dto.WxBindDTO;
import com.community.front.dto.WxLoginDTO;
import com.community.front.service.ResidentAuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 前台居民认证接口（登录/注册/找回密码/微信静默登录）
 */
@RestController
@RequestMapping("/api/residentAuth")
@Api(tags = "前台-居民认证")
public class ResidentAuthController {

    @Autowired
    private ResidentAuthService residentAuthService;

    @PostMapping("/sendSmsCode")
    @ApiOperation("获取手机验证码（type：1-注册 2-登录 3-找回密码）")
    public Result<Map<String, Object>> sendSmsCode(
            @ApiParam(value = "手机号", required = true) @RequestParam String phone,
            @ApiParam(value = "验证码类型") @RequestParam(required = false) Integer type) {
        return Result.success(residentAuthService.sendSmsCode(phone, type));
    }

    @PostMapping("/verifySmsCode")
    @ApiOperation("校验手机号+验证码")
    public Result<Boolean> verifySmsCode(
            @ApiParam(value = "手机号", required = true) @RequestParam String phone,
            @ApiParam(value = "验证码", required = true) @RequestParam String code) {
        return Result.success(residentAuthService.verifySmsCode(phone, code));
    }

    @PostMapping("/loginBySms")
    @ApiOperation("手机号+验证码登录")
    public Result<Map<String, Object>> loginBySms(@RequestBody SmsLoginDTO smsLoginDTO) {
        return Result.success(residentAuthService.loginBySms(smsLoginDTO));
    }

    @PostMapping("/loginByPassword")
    @ApiOperation("手机号+密码登录")
    public Result<Map<String, Object>> loginByPassword(@RequestBody PasswordLoginDTO passwordLoginDTO) {
        return Result.success(residentAuthService.loginByPassword(passwordLoginDTO));
    }

    @PostMapping("/register")
    @ApiOperation("居民注册")
    public Result<String> register(@RequestBody RegisterDTO registerDTO) {
        residentAuthService.register(registerDTO);
        return Result.success("注册成功");
    }

    @PostMapping("/resetPassword")
    @ApiOperation("找回密码（重置密码）")
    public Result<String> resetPassword(@RequestBody RegisterDTO registerDTO) {
        residentAuthService.resetPassword(registerDTO);
        return Result.success("密码重置成功");
    }

    @PostMapping("/wxLogin")
    @ApiOperation("微信静默登录（code换openid，已绑定直接签发token）")
    public Result<Map<String, Object>> wxLogin(@RequestBody WxLoginDTO wxLoginDTO) {
        return Result.success(residentAuthService.wxLogin(wxLoginDTO));
    }

    @PostMapping("/wxBind")
    @ApiOperation("微信绑定居民账号（手机号+验证码）")
    public Result<Map<String, Object>> wxBind(@RequestBody WxBindDTO wxBindDTO) {
        return Result.success(residentAuthService.wxBind(wxBindDTO));
    }

    @GetMapping("/health")
    @ApiOperation("健康检查")
    public Result<String> health() {
        return Result.success("community_front ok");
    }
}
