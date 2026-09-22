package com.community.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 客服实体类
 */
@Data
@TableName("customer_service")
@ApiModel(description = "客服")
public class CustomerService {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "客服ID")
    private Integer csId;

    @ApiModelProperty(value = "客服姓名")
    private String csName;

    @ApiModelProperty(value = "职位")
    private String position;

    @ApiModelProperty(value = "联系电话")
    private String phone;

    @ApiModelProperty(value = "微信号")
    private String wechat;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
