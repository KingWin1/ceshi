package com.community.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 房屋信息VO（多表查询结果）
 */
@Data
@ApiModel(description = "房屋信息")
public class HouseVO {

    @ApiModelProperty(value = "房屋ID")
    private Integer houseId;

    @ApiModelProperty(value = "楼栋名称")
    private String buildingName;

    @ApiModelProperty(value = "单元号")
    private String unitNo;

    @ApiModelProperty(value = "房号")
    private String houseNumber;

    @ApiModelProperty(value = "面积")
    private BigDecimal area;

    @ApiModelProperty(value = "户型（如：3室2厅1卫）")
    private String houseTypeDesc;

    @ApiModelProperty(value = "朝向：1-东 2-南 3-西 4-北 5-东北 6-东南 7-西南 8-西北")
    private Integer orientation;

    @ApiModelProperty(value = "业主姓名")
    private String ownerName;

    @ApiModelProperty(value = "业主电话")
    private String ownerPhone;

    @ApiModelProperty(value = "状态：1-空置 2-已入住 3-出租")
    private Integer status;
}
