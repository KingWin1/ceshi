package com.community.backend.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 首页账单列表VO（账单-费用类型-房屋-楼栋 联查）
 */
@Data
@ApiModel(description = "首页账单条目")
public class HomeBillVO {

    @ApiModelProperty(value = "账单ID")
    private Integer billId;

    @ApiModelProperty(value = "账单编号")
    private String billNo;

    @ApiModelProperty(value = "应付金额")
    private BigDecimal payableAmount;

    @ApiModelProperty(value = "已付金额")
    private BigDecimal paidAmount;

    @ApiModelProperty(value = "状态：1-已缴 2-未缴")
    private Integer status;

    @ApiModelProperty(value = "支付时间")
    private Date payTime;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "费用类型名称")
    private String feeTypeName;

    @ApiModelProperty(value = "楼栋名称")
    private String buildingName;
}
