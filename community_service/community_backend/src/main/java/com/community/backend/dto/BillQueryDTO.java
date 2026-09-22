package com.community.backend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 账单查询参数DTO
 */
@Data
@ApiModel(description = "账单查询参数")
public class BillQueryDTO {

    @ApiModelProperty(value = "状态：1-已缴 2-未缴")
    private Integer status;

    @ApiModelProperty(value = "业主名/电话/备注（模糊查询）")
    private String keyword;

    @ApiModelProperty(value = "楼栋ID")
    private Integer buildingId;

    @ApiModelProperty(value = "单元号")
    private String unitNo;

    @ApiModelProperty(value = "房间号")
    private String houseNumber;

    @ApiModelProperty(value = "费用类型ID")
    private Integer feeTypeId;

    @ApiModelProperty(value = "计费周期")
    private String billingPeriod;

    @ApiModelProperty(value = "计费周期值：按月-月份(1-12)，按季度-季度(1-4)")
    private Integer billingPeriodValue;

    @ApiModelProperty(value = "计费年份")
    private Integer billingYear;

    @ApiModelProperty(value = "当前页码", required = true)
    private Integer pageNum = 1;

    @ApiModelProperty(value = "页面大小", required = true)
    private Integer pageSize = 10;
}
