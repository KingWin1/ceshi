package com.community.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 居民认证统计VO（按居民ID分组统计认证次数）
 */
@Data
@ApiModel(description = "居民认证统计")
public class ResidentAuthCountVO {

    @ApiModelProperty(value = "居民ID")
    private Integer residentId;

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "房屋名称（首个认证房屋，如：三水别墅-1单元-123）")
    private String houseName;

    @ApiModelProperty(value = "认证记录次数")
    private Integer authCount;
}
