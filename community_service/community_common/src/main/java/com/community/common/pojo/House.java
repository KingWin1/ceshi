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
 * 房屋实体类
 */
@Data
@TableName("house")
@ApiModel(description = "房屋")
public class House {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "房屋ID")
    private Integer houseId;

    @ApiModelProperty(value = "楼栋ID")
    private Integer buildingId;

    @ApiModelProperty(value = "单元号")
    private String unitNo;

    @ApiModelProperty(value = "房屋号")
    private String houseNumber;

    @ApiModelProperty(value = "面积")
    private BigDecimal area;

    @ApiModelProperty(value = "户型ID")
    private Integer houseTypeId;

    @ApiModelProperty(value = "房型：1-住宅 2-商铺 3-车位")
    private Integer roomType;

    @ApiModelProperty(value = "朝向：1-东 2-南 3-西 4-北 5-东北 6-东南 7-西南 8-西北")
    private Integer orientation;

    @ApiModelProperty(value = "状态：1-空置 2-已入住 3-出租")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
