package com.community.backend.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 账单信息VO（多表查询结果）
 */
@Data
@ApiModel(description = "账单信息")
public class BillVO {

    @ApiModelProperty(value = "账单ID")
    private Integer billId;

    @ApiModelProperty(value = "账单编号")
    private String billNo;

    @ApiModelProperty(value = "楼栋名称")
    private String buildingName;

    @ApiModelProperty(value = "单元号")
    private String unitNo;

    @ApiModelProperty(value = "房间号")
    private String houseNumber;

    @ApiModelProperty(value = "完整房号（楼栋名称-单元号-房间号）")
    private String fullHouseNo;

    @ApiModelProperty(value = "居民ID")
    private Integer residentId;

    @ApiModelProperty(value = "居民姓名")
    private String residentName;

    @ApiModelProperty(value = "电话")
    private String phone;

    @ApiModelProperty(value = "费用类型名称")
    private String feeTypeName;

    @ApiModelProperty(value = "单价（按计费周期取对应费用类型单价）")
    private BigDecimal unitPrice;

    @ApiModelProperty(value = "面积")
    private BigDecimal area;

    @ApiModelProperty(value = "账单金额")
    private BigDecimal billAmount;

    @ApiModelProperty(value = "优惠金额")
    private BigDecimal discountAmount;

    @ApiModelProperty(value = "应付金额")
    private BigDecimal payableAmount;

    @ApiModelProperty(value = "已付金额")
    private BigDecimal paidAmount;

    @ApiModelProperty(value = "支付方式：1-微信 2-支付宝 3-现金 4-银行转账")
    private Integer payMethod;

    @ApiModelProperty(value = "支付时间")
    private Date payTime;

    @ApiModelProperty(value = "支付流水号")
    private String paySerialNo;

    @ApiModelProperty(value = "计费周期")
    private String billingPeriod;

    @ApiModelProperty(value = "计费周期值：按月-月份(1-12)，按季度-季度(1-4)，按年-年份")
    private Integer billingPeriodValue;

    @ApiModelProperty(value = "计费开始日期")
    private Date startDate;

    @ApiModelProperty(value = "计费结束日期")
    private Date endDate;

    @ApiModelProperty(value = "催缴次数")
    private Integer remindCount;

    @ApiModelProperty(value = "最后催缴时间")
    private Date lastRemindTime;

    @ApiModelProperty(value = "状态：1-已缴 2-未缴")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
