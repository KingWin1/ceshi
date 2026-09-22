package com.community.backend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 员工查询参数DTO
 */
@Data
@ApiModel(description = "员工查询参数")
public class EmployeeQueryDTO {

    @ApiModelProperty(value = "关键词（姓名/手机号 模糊查询）")
    private String keyword;

    @ApiModelProperty(value = "员工姓名（模糊查询）")
    private String employeeName;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "部门ID")
    private Integer deptId;

    @ApiModelProperty(value = "岗位ID")
    private Integer positionId;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "当前页码", required = true)
    private Integer pageNum = 1;

    @ApiModelProperty(value = "页面大小", required = true)
    private Integer pageSize = 10;
}
