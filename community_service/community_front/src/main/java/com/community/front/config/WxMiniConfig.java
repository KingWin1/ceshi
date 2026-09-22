package com.community.front.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信小程序配置类（读取 yml 中 wx.mini.* 配置）
 */
@Data
@Component
@ConfigurationProperties(prefix = "wx.mini")
public class WxMiniConfig {

    /** 小程序 appid */
    private String appid;

    /** 小程序密钥 */
    private String secret;

    /** Mock开关：true=不请求微信服务器，开发调试用 */
    private Boolean mockEnabled = true;
}
