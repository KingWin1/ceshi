package com.community.backend.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 部门岗位统计VO（按部门ID分组统计岗位数量）
 */
@Data
@ApiModel(description = "部门岗位统计")
public class DeptPositionCountVO {

    @ApiModelProperty(value = "部门ID")
    private Integer deptId;

    @ApiModelProperty(value = "部门名称")
    private String deptName;

    @ApiModelProperty(value = "岗位记录数量")
    private Integer positionCount;
}
