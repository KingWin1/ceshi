package com.community.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 居民查询参数DTO
 */
@Data
@ApiModel(description = "居民查询参数")
public class ResidentQueryDTO {

    @ApiModelProperty(value = "居民类型：1-业主 2-家属 3-租户")
    private Integer type;

    @ApiModelProperty(value = "姓名（模糊查询）")
    private String name;

    @ApiModelProperty(value = "关键字：姓名/手机号/身份证（模糊查询）")
    private String keyword;

    @ApiModelProperty(value = "性别：0-男 1-女 2-未知")
    private Integer gender;

    @ApiModelProperty(value = "楼栋ID")
    private Integer buildingId;

    @ApiModelProperty(value = "单元号")
    private String unitNo;

    @ApiModelProperty(value = "房间号")
    private String houseNumber;

    @ApiModelProperty(value = "注册方式：1-App注册 2-后台注册")
    private Integer registerWay;

    @ApiModelProperty(value = "状态：1-正常 2-停用")
    private Integer status;

    @ApiModelProperty(value = "当前页码", required = true)
    private Integer pageNum = 1;

    @ApiModelProperty(value = "页面大小", required = true)
    private Integer pageSize = 10;
}
