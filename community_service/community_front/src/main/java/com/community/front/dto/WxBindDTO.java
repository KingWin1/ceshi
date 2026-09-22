package com.community.front.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 微信绑定居民账号参数（手机号+验证码方式）
 */
@Data
@ApiModel(description = "微信绑定居民账号参数")
public class WxBindDTO {

    @ApiModelProperty(value = "微信openid", required = true)
    private String openid;

    @ApiModelProperty(value = "手机号", required = true)
    private String phone;

    @ApiModelProperty(value = "验证码", required = true)
    private String code;
}
