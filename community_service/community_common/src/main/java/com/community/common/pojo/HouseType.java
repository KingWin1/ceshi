package com.community.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 户型实体类
 */
@Data
@TableName("house_type")
@ApiModel(description = "户型")
public class HouseType {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "户型ID")
    private Integer houseTypeId;

    @ApiModelProperty(value = "室")
    private Integer rooms;

    @ApiModelProperty(value = "厅")
    private Integer hall;

    @ApiModelProperty(value = "卫")
    private Integer toilet;
}
