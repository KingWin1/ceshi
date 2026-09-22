package com.community.backend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 房屋添加/修改参数DTO
 */
@Data
@ApiModel(description = "房屋添加/修改参数")
public class HouseSaveDTO {

    @ApiModelProperty(value = "房屋ID（修改时必填）")
    private Integer houseId;

    @ApiModelProperty(value = "楼栋ID", required = true)
    private Integer buildingId;

    @ApiModelProperty(value = "单元号", required = true)
    private String unitNo;

    @ApiModelProperty(value = "房屋号", required = true)
    private String houseNumber;

    @ApiModelProperty(value = "面积")
    private BigDecimal area;

    @ApiModelProperty(value = "房型：1-住宅 2-商铺 3-车位")
    private Integer roomType;

    @ApiModelProperty(value = "朝向：1-东 2-南 3-西 4-北 5-东北 6-东南 7-西南 8-西北")
    private Integer orientation;

    @ApiModelProperty(value = "状态：1-空置 2-已入住 3-出租")
    private Integer status;

    @ApiModelProperty(value = "户型-室")
    private Integer rooms;

    @ApiModelProperty(value = "户型-厅")
    private Integer hall;

    @ApiModelProperty(value = "户型-卫")
    private Integer toilet;
}
