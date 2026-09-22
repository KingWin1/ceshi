package com.community.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 居民信息VO（多表查询结果）
 */
@Data
@ApiModel(description = "居民信息")
public class ResidentVO {

    @ApiModelProperty(value = "居民ID")
    private Integer residentId;

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "电话")
    private String phone;

    @ApiModelProperty(value = "身份证")
    private String idCard;

    @ApiModelProperty(value = "性别：0-男 1-女 2-未知")
    private Integer gender;

    @ApiModelProperty(value = "头像地址")
    private String avatarUrl;

    @ApiModelProperty(value = "房屋名称（如：三水别墅-1单元-123）")
    private String houseName;

    @ApiModelProperty(value = "房屋数（该居民关联的房屋数量）")
    private Integer houseCount;

    @ApiModelProperty(value = "类型：1-业主 2-家属 3-租户")
    private Integer type;

    @ApiModelProperty(value = "注册方式：1-App注册 2-后台注册")
    private Integer registerWay;

    @ApiModelProperty(value = "状态：1-正常 2-停用")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
}
