package com.community.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 居民实体类
 */
@Data
@TableName("resident")
@ApiModel(description = "居民")
public class Resident {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "居民ID")
    private Integer residentId;

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "身份证")
    private String idCard;

    @ApiModelProperty(value = "性别：0-男 1-女 2-未知")
    private Integer gender;

    @ApiModelProperty(value = "类型：1-业主 2-家属 3-租户")
    private Integer type;

    @ApiModelProperty(value = "注册方式：1-App注册 2-后台注册")
    private Integer registerWay;

    @ApiModelProperty(value = "头像地址")
    private String avatarUrl;

    @ApiModelProperty(value = "状态：1-正常 2-停用")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
