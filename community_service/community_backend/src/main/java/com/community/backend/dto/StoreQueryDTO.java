package com.community.backend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 门店查询参数DTO
 */
@Data
@ApiModel(description = "门店查询参数")
public class StoreQueryDTO {

    @ApiModelProperty(value = "门店ID")
    private Integer storeId;

    @ApiModelProperty(value = "门店名称（模糊查询）")
    private String storeName;

    @ApiModelProperty(value = "详细地址（模糊查询）")
    private String address;

    @ApiModelProperty(value = "联系电话（模糊查询）")
    private String contactPhone;

    @ApiModelProperty(value = "门店介绍（模糊查询）")
    private String introduction;

    @ApiModelProperty(value = "关键词（名称/地址/电话/介绍 模糊查询）")
    private String keyword;

    @ApiModelProperty(value = "分类ID")
    private Integer categoryId;

    @ApiModelProperty(value = "状态：1-营业中 2-已下架")
    private Integer status;

    @ApiModelProperty(value = "推荐：1-推荐 2-普通")
    private Integer isRecommend;

    @ApiModelProperty(value = "当前页码", required = true)
    private Integer pageNum = 1;

    @ApiModelProperty(value = "页面大小", required = true)
    private Integer pageSize = 10;
}
