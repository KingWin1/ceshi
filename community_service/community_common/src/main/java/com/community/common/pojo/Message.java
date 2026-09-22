package com.community.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 消息实体类
 */
@Data
@TableName("message")
@ApiModel(description = "消息")
public class Message {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "消息ID")
    private Integer messageId;

    @ApiModelProperty(value = "账单ID")
    private Integer billId;

    @ApiModelProperty(value = "居民ID")
    private Integer residentId;

    @ApiModelProperty(value = "消息标题")
    private String title;

    @ApiModelProperty(value = "消息内容")
    private String content;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
