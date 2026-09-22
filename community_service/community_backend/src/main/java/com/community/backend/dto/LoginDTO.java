package com.community.backend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 管理员登录请求DTO
 */
@Data
@ApiModel(description = "管理员登录请求")
public class LoginDTO {

    @NotBlank(message = "用户名不能为空")
    @ApiModelProperty(value = "用户名", required = true)
    private String username;

    @NotBlank(message = "密码不能为空")
    @ApiModelProperty(value = "密码", required = true)
    private String password;

    @NotBlank(message = "验证码不能为空")
    @ApiModelProperty(value = "验证码", required = true)
    private String code;

    @NotBlank(message = "验证码标识不能为空")
    @ApiModelProperty(value = "验证码标识（uuid）", required = true)
    private String uuid;
}
