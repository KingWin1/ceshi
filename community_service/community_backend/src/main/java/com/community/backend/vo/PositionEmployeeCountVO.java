package com.community.backend.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 岗位员工统计VO（按岗位ID分组统计员工数量）
 */
@Data
@ApiModel(description = "岗位员工统计")
public class PositionEmployeeCountVO {

    @ApiModelProperty(value = "岗位ID")
    private Integer positionId;

    @ApiModelProperty(value = "岗位名称")
    private String positionName;

    @ApiModelProperty(value = "员工记录数量")
    private Integer employeeCount;
}
