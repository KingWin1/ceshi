package com.community.front.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 手机号+验证码登录参数
 */
@Data
@ApiModel(description = "手机号验证码登录参数")
public class SmsLoginDTO {

    @ApiModelProperty(value = "手机号", required = true)
    private String phone;

    @ApiModelProperty(value = "验证码", required = true)
    private String code;
}
