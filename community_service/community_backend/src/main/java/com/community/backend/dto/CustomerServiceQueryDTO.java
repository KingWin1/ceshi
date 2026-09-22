package com.community.backend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 客服查询参数DTO
 */
@Data
@ApiModel(description = "客服查询参数")
public class CustomerServiceQueryDTO {

    @ApiModelProperty(value = "客服姓名（模糊查询）")
    private String csName;

    @ApiModelProperty(value = "职位（模糊查询）")
    private String position;

    @ApiModelProperty(value = "联系电话（模糊查询）")
    private String phone;

    @ApiModelProperty(value = "微信号（模糊查询）")
    private String wechat;

    @ApiModelProperty(value = "当前页码", required = true)
    private Integer pageNum = 1;

    @ApiModelProperty(value = "页面大小", required = true)
    private Integer pageSize = 10;
}
