package com.community.backend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 导入账单行数据DTO（Excel每行解析结果）
 */
@Data
@ApiModel(description = "导入账单行数据")
public class BillImportRowDTO {

    @ApiModelProperty(value = "账单编号")
    private String billNo;

    @ApiModelProperty(value = "楼栋名称")
    private String buildingName;

    @ApiModelProperty(value = "单元号")
    private String unitNo;

    @ApiModelProperty(value = "房间号")
    private String houseNumber;

    @ApiModelProperty(value = "费用类型名称")
    private String feeTypeName;

    @ApiModelProperty(value = "计费周期：month/quarter/year")
    private String billingPeriod;

    @ApiModelProperty(value = "计费周期值：按月-月份(1-12)，按季度-季度(1-4)")
    private Integer billingPeriodValue;

    @ApiModelProperty(value = "计费开始日期")
    private Date startDate;

    @ApiModelProperty(value = "计费结束日期")
    private Date endDate;

    @ApiModelProperty(value = "账单金额")
    private BigDecimal billAmount;

    @ApiModelProperty(value = "优惠金额")
    private BigDecimal discountAmount;
}
