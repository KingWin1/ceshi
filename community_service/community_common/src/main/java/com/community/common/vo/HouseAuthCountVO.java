package com.community.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 房屋认证统计VO（按房屋ID分组统计认证次数）
 */
@Data
@ApiModel(description = "房屋认证统计")
public class HouseAuthCountVO {

    @ApiModelProperty(value = "房屋ID")
    private Integer houseId;

    @ApiModelProperty(value = "楼栋名称")
    private String buildingName;

    @ApiModelProperty(value = "单元号")
    private String unitNo;

    @ApiModelProperty(value = "房号")
    private String houseNumber;

    @ApiModelProperty(value = "认证记录次数")
    private Integer authCount;
}
