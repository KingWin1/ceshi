package com.community.front.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 注册/重置密码参数
 */
@Data
@ApiModel(description = "注册/重置密码参数")
public class RegisterDTO {

    @ApiModelProperty(value = "手机号", required = true)
    private String phone;

    @ApiModelProperty(value = "验证码", required = true)
    private String code;

    @ApiModelProperty(value = "密码", required = true)
    private String password;
}
