package com.community.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 投诉记录实体类
 */
@Data
@TableName("complaint_record")
@ApiModel(description = "投诉记录")
public class ComplaintRecord {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "投诉记录ID")
    private Integer complaintId;

    @ApiModelProperty(value = "投诉记录编号")
    private String complaintNo;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "联系人")
    private String contactPerson;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "联系电话")
    private String contactPhone;

    @ApiModelProperty(value = "附件图片地址")
    private String attachmentUrl;

    @ApiModelProperty(value = "状态：1-待处理 2-处理中 3-已回复 4-已关闭")
    private Integer status;

    @ApiModelProperty(value = "提交时间")
    private Date submitTime;
}
