package com.community.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 报修类型实体类
 */
@Data
@TableName("repair_type")
@ApiModel(description = "报修类型")
public class RepairType {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "类型ID")
    private Integer typeId;

    @ApiModelProperty(value = "类型名称")
    private String typeName;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
