package com.community.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 房屋查询参数DTO
 */
@Data
@ApiModel(description = "房屋查询参数")
public class HouseQueryDTO {

    @ApiModelProperty(value = "楼栋ID")
    private Integer buildingId;

    @ApiModelProperty(value = "单元号")
    private String unitNo;

    @ApiModelProperty(value = "状态：1-空置 2-已入住 3-出租")
    private Integer status;

    @ApiModelProperty(value = "最小面积")
    private BigDecimal minArea;

    @ApiModelProperty(value = "最大面积")
    private BigDecimal maxArea;

    @ApiModelProperty(value = "室的个数")
    private Integer rooms;

    @ApiModelProperty(value = "厅的个数")
    private Integer hall;

    @ApiModelProperty(value = "卫的个数")
    private Integer toilet;

    @ApiModelProperty(value = "朝向：1-东 2-南 3-西 4-北 5-东北 6-东南 7-西南 8-西北")
    private Integer orientation;

    @ApiModelProperty(value = "业主姓名（模糊查询）")
    private String ownerName;

    @ApiModelProperty(value = "居民姓名/电话/房间号（模糊查询）")
    private String keyword;

    @ApiModelProperty(value = "当前页码", required = true)
    private Integer pageNum = 1;

    @ApiModelProperty(value = "页面大小", required = true)
    private Integer pageSize = 10;
}
