package com.community.front.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 手机号+密码登录参数
 */
@Data
@ApiModel(description = "手机号密码登录参数")
public class PasswordLoginDTO {

    @ApiModelProperty(value = "手机号", required = true)
    private String phone;

    @ApiModelProperty(value = "密码", required = true)
    private String password;
}
