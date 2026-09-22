package com.community.backend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 公告查询参数DTO
 */
@Data
@ApiModel(description = "公告查询参数")
public class NoticeQueryDTO {

    @ApiModelProperty(value = "公告标题（模糊查询）")
    private String title;

    @ApiModelProperty(value = "状态：1-已发布 2-已下架")
    private Integer status;

    @ApiModelProperty(value = "是否置顶：1-是 2-否")
    private Integer isTop;

    @ApiModelProperty(value = "当前页码", required = true)
    private Integer pageNum = 1;

    @ApiModelProperty(value = "页面大小", required = true)
    private Integer pageSize = 10;
}
