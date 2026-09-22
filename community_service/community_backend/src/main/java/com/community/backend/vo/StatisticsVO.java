package com.community.backend.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 首页统计卡片VO
 */
@Data
@ApiModel(description = "首页统计卡片数据")
public class StatisticsVO {

    @ApiModelProperty(value = "本月应收总额")
    private BigDecimal monthReceivable;

    @ApiModelProperty(value = "上月应收总额")
    private BigDecimal lastMonthReceivable;

    @ApiModelProperty(value = "本月实收总额")
    private BigDecimal monthReceived;

    @ApiModelProperty(value = "缴费率（%）")
    private BigDecimal paidRate;

    @ApiModelProperty(value = "应收较上月增幅（%）")
    private BigDecimal receivableGrowth;

    @ApiModelProperty(value = "待缴账单笔数")
    private Integer unpaidCount;

    @ApiModelProperty(value = "逾期账单笔数")
    private Integer overdueCount;

    @ApiModelProperty(value = "本月新增待缴笔数")
    private Integer unpaidMonthCount;

    @ApiModelProperty(value = "待处理报修单数")
    private Integer pendingRepairCount;

    @ApiModelProperty(value = "报修待派单数")
    private Integer repairDispatchCount;

    @ApiModelProperty(value = "报修处理中数")
    private Integer repairHandlingCount;

    @ApiModelProperty(value = "待处理投诉条数")
    private Integer pendingComplaintCount;

    @ApiModelProperty(value = "投诉待回复数")
    private Integer complaintReplyCount;

    @ApiModelProperty(value = "投诉处理中数")
    private Integer complaintHandlingCount;

    @ApiModelProperty(value = "楼栋总数")
    private Integer buildingCount;

    @ApiModelProperty(value = "房屋总数")
    private Integer houseCount;

    @ApiModelProperty(value = "房屋入住率（%）")
    private BigDecimal occupancyRate;

    @ApiModelProperty(value = "已入住房屋数")
    private Integer occupiedCount;

    @ApiModelProperty(value = "空置房屋数")
    private Integer vacantCount;

    @ApiModelProperty(value = "注册居民总数")
    private Integer residentCount;

    @ApiModelProperty(value = "本月新增居民数")
    private Integer residentMonthCount;

    @ApiModelProperty(value = "本月未收金额")
    private BigDecimal unpaidAmount;
}
