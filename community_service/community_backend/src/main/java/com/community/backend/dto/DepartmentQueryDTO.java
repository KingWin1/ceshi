package com.community.backend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 部门查询参数DTO
 */
@Data
@ApiModel(description = "部门查询参数")
public class DepartmentQueryDTO {

    @ApiModelProperty(value = "部门名称（模糊查询）")
    private String deptName;

    @ApiModelProperty(value = "当前页码", required = true)
    private Integer pageNum = 1;

    @ApiModelProperty(value = "页面大小", required = true)
    private Integer pageSize = 10;
}
