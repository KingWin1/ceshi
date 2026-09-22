package com.community.front.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 微信静默登录参数
 */
@Data
@ApiModel(description = "微信静默登录参数")
public class WxLoginDTO {

    @ApiModelProperty(value = "wx.login 获取的凭证code", required = true)
    private String code;

    @ApiModelProperty(value = "微信获取的用户信息（昵称、头像等JSON）")
    private String userInfo;

    @ApiModelProperty(value = "小程序appid")
    private String appid;

    @ApiModelProperty(value = "小程序secret")
    private String secret;
}
