package com.community.front.utils;

import com.community.common.exception.BusinessException;
import com.community.front.config.WxMiniConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 微信开放接口工具类
 * 提供 code 换取 openid/session_key 的能力，
 * Mock 模式下不请求微信服务器，用 code 生成稳定的假 openid（开发调试用）
 */
@Component
public class WxApiUtil {

    /** code2Session 接口地址 */
    private static final String CODE2SESSION_URL =
            "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";

    /** Mock模式固定openid：wx.login的code每次不同，若用code拼openid会导致每次登录都被当成新用户，无法复用绑定关系 */
    private static final String MOCK_OPENID = "mock_openid_dev";

    @Autowired
    private WxMiniConfig wxMiniConfig;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 用 wx.login 的 code 换取 openid
     * @param code 小程序登录凭证
     * @return openid
     */
    public String getOpenid(String code) {
        // Mock模式：直接根据 code 生成固定格式假 openid，便于本地调试
        if (Boolean.TRUE.equals(wxMiniConfig.getMockEnabled())) {
            return "mock_openid_" + code;
        }
        String url = String.format(CODE2SESSION_URL,
                wxMiniConfig.getAppid(), wxMiniConfig.getSecret(), code);
        String resp = restTemplate.getForObject(url, String.class);
        if (resp == null || !resp.contains("openid")) {
            throw new BusinessException("微信登录失败，请重试");
        }
        // 从返回JSON中提取 openid（形如 {"openid":"xxx","session_key":"xxx"}）
        return extractJsonValue(resp, "openid");
    }

    /**
     * 从简单JSON中提取字符串字段值（避免为此引入额外JSON库依赖）
     */
    private String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\":\"";
        int start = json.indexOf(searchKey);
        if (start < 0) {
            return null;
        }
        start += searchKey.length();
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }
}
