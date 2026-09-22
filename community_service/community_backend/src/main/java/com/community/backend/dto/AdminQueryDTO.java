package com.community.backend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 管理员查询参数DTO
 */
@Data
@ApiModel(description = "管理员查询参数")
public class AdminQueryDTO {

    @ApiModelProperty(value = "关键词（用户名/姓名/手机号 模糊查询）")
    private String keyword;

    @ApiModelProperty(value = "用户名（模糊查询）")
    private String username;

    @ApiModelProperty(value = "姓名（模糊查询）")
    private String name;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "角色ID")
    private Integer roleId;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "当前页码", required = true)
    private Integer pageNum = 1;

    @ApiModelProperty(value = "页面大小", required = true)
    private Integer pageSize = 10;
}
