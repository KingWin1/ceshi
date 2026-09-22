package com.community.backend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 广告查询参数DTO
 */
@Data
@ApiModel(description = "广告查询参数")
public class AdvertisementQueryDTO {

    @ApiModelProperty(value = "广告类型：1-启动页 2-首页轮播图 3-弹窗广告")
    private Integer adType;

    @ApiModelProperty(value = "状态：1-启用 2-停用")
    private Integer status;

    @ApiModelProperty(value = "当前页码", required = true)
    private Integer pageNum = 1;

    @ApiModelProperty(value = "页面大小", required = true)
    private Integer pageSize = 10;
}
