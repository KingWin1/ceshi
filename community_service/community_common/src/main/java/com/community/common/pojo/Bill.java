package com.community.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 账单实体类
 */
@Data
@TableName("bill")
@ApiModel(description = "账单")
public class Bill {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "账单ID")
    private Integer billId;

    @ApiModelProperty(value = "账单编号")
    private String billNo;

    @ApiModelProperty(value = "房屋ID")
    private Integer houseId;

    @ApiModelProperty(value = "费用ID")
    private Integer feeTypeId;

    @ApiModelProperty(value = "账单金额")
    private BigDecimal billAmount;

    @ApiModelProperty(value = "优惠金额")
    private BigDecimal discountAmount;

    @ApiModelProperty(value = "应付金额")
    private BigDecimal payableAmount;

    @ApiModelProperty(value = "已付金额")
    private BigDecimal paidAmount;

    @ApiModelProperty(value = "计费周期（如 month, quarter, year）")
    private String billingPeriod;

    @ApiModelProperty(value = "计费周期的值（如月份数）")
    private Integer billingPeriodValue;

    @ApiModelProperty(value = "计费开始日期")
    private Date startDate;

    @ApiModelProperty(value = "计费结束日期")
    private Date endDate;

    @ApiModelProperty(value = "状态：1-已缴 2-未缴")
    private Integer status;

    @ApiModelProperty(value = "支付方式：1-微信支付 2-支付宝 3-现金 4-银行转账")
    private Integer payMethod;

    @ApiModelProperty(value = "支付时间")
    private Date payTime;

    @ApiModelProperty(value = "支付流水号")
    private String paySerialNo;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
