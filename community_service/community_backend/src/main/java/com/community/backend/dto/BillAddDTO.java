package com.community.backend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 添加账单参数DTO
 */
@Data
@ApiModel(description = "添加账单参数")
public class BillAddDTO {

    @ApiModelProperty(value = "账单编号")
    private String billNo;

    @ApiModelProperty(value = "房屋ID", required = true)
    private Integer houseId;

    @ApiModelProperty(value = "费用类型ID", required = true)
    private Integer feeTypeId;

    @ApiModelProperty(value = "账单金额", required = true)
    private BigDecimal billAmount;

    @ApiModelProperty(value = "优惠金额")
    private BigDecimal discountAmount;

    @ApiModelProperty(value = "应付金额", required = true)
    private BigDecimal payableAmount;

    @ApiModelProperty(value = "已付金额")
    private BigDecimal paidAmount;

    @ApiModelProperty(value = "计费周期", required = true)
    private String billingPeriod;

    @ApiModelProperty(value = "计费周期值")
    private Integer billingPeriodValue;

    @ApiModelProperty(value = "计费开始日期", required = true)
    private Date startDate;

    @ApiModelProperty(value = "计费结束日期", required = true)
    private Date endDate;

    @ApiModelProperty(value = "状态：1-已缴 2-未缴")
    private Integer status;
}
