package com.community.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 楼栋实体类
 */
@Data
@TableName("building")
@ApiModel(description = "楼栋")
public class Building {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "楼栋ID")
    private Integer buildingId;

    @ApiModelProperty(value = "楼栋名称")
    private String buildingName;

    @ApiModelProperty(value = "单元数")
    private Integer unitCount;

    @ApiModelProperty(value = "楼层数")
    private Integer floorCount;

    @ApiModelProperty(value = "房间总数")
    private Integer totalRooms;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
