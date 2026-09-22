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
 * 费用类型实体类
 */
@Data
@TableName("fee_type")
@ApiModel(description = "费用类型")
public class FeeType {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "费用类型ID")
    private Integer feeTypeId;

    @ApiModelProperty(value = "费用类型名称")
    private String feeTypeName;

    @ApiModelProperty(value = "编码")
    private String code;

    @ApiModelProperty(value = "计费方式：1-按面积 2-固定金额")
    private Integer billingMethod;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "状态：1-启用 2-禁用")
    private Integer status;

    @ApiModelProperty(value = "按月计价")
    private BigDecimal monthlyPrice;

    @ApiModelProperty(value = "按季度计价")
    private BigDecimal quarterlyPrice;

    @ApiModelProperty(value = "按年计价")
    private BigDecimal yearlyPrice;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
