package com.community.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 微信小程序用户身份实体类
 */
@Data
@TableName("resident_wx")
@ApiModel(description = "微信小程序用户身份")
public class ResidentWx {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键ID")
    private Integer id;

    @ApiModelProperty(value = "微信openid")
    private String openid;

    @ApiModelProperty(value = "开放平台unionid")
    private String unionid;

    @ApiModelProperty(value = "关联居民ID，NULL表示未绑定")
    private Integer residentId;

    @ApiModelProperty(value = "最近一次session_key")
    private String sessionKey;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
}
