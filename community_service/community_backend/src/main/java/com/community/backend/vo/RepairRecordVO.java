package com.community.backend.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 报修记录VO（多表查询结果）
 */
@Data
@ApiModel(description = "报修记录")
public class RepairRecordVO {

    @ApiModelProperty(value = "报修ID")
    private Integer repairId;

    @ApiModelProperty(value = "报修单号")
    private String repairNo;

    @ApiModelProperty(value = "报修人")
    private String reporterName;

    @ApiModelProperty(value = "联系电话")
    private String contactPhone;

    @ApiModelProperty(value = "报修地址")
    private String repairAddress;

    @ApiModelProperty(value = "报修类型名称")
    private String repairTypeName;

    @ApiModelProperty(value = "问题描述")
    private String description;

    @ApiModelProperty(value = "员工姓名")
    private String employeeName;

    @ApiModelProperty(value = "评价")
    private String evaluation;

    @ApiModelProperty(value = "期望上门时间")
    private Date expectedTime;

    @ApiModelProperty(value = "状态：1-待派单 2-已派单 3-处理中 4-已完成 5-已取消")
    private Integer status;

    @ApiModelProperty(value = "报修时间")
    private Date createTime;

    @ApiModelProperty(value = "派单时间")
    private Date dispatchTime;

    @ApiModelProperty(value = "完成时间")
    private Date completeTime;

    @ApiModelProperty(value = "完工备注")
    private String completeRemark;

    @ApiModelProperty(value = "完工图片地址")
    private String completeImageUrl;

    @ApiModelProperty(value = "评价分数(1-5)")
    private Integer ratingScore;

    @ApiModelProperty(value = "评价时间")
    private Date ratingTime;

    @ApiModelProperty(value = "评价内容")
    private String ratingContent;
}
