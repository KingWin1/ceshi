package com.community.backend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 楼栋查询参数DTO
 */
@Data
@ApiModel(description = "楼栋查询参数")
public class BuildingQueryDTO {

    @ApiModelProperty(value = "楼栋名称（模糊查询）")
    private String buildingName;

    @ApiModelProperty(value = "当前页码", required = true)
    private Integer pageNum = 1;

    @ApiModelProperty(value = "页面大小", required = true)
    private Integer pageSize = 10;
}
