package com.community.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 回复信息实体类
 */
@Data
@TableName("reply_info")
@ApiModel(description = "回复信息")
public class ReplyInfo {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "ID")
    private Integer id;

    @ApiModelProperty(value = "投诉记录ID")
    private Integer complaintId;

    @ApiModelProperty(value = "回复人类型：1-用户 2-管理员")
    private Integer replyType;

    @ApiModelProperty(value = "回复人ID")
    private Integer replyerId;

    @ApiModelProperty(value = "回复消息")
    private String replyMessage;

    @ApiModelProperty(value = "回复图片地址")
    private String replyImageUrl;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
