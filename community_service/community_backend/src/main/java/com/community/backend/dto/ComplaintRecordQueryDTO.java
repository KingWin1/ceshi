package com.community.backend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 投诉记录查询参数DTO
 */
@Data
@ApiModel(description = "投诉记录查询参数")
public class ComplaintRecordQueryDTO {

    @ApiModelProperty(value = "标题（模糊查询）")
    private String title;

    @ApiModelProperty(value = "联系人（模糊查询）")
    private String contactPerson;

    @ApiModelProperty(value = "关键词（标题/联系人/联系电话 模糊查询）")
    private String keyword;

    @ApiModelProperty(value = "状态：1-待处理 2-处理中 3-已回复 4-已关闭")
    private Integer status;

    @ApiModelProperty(value = "当前页码", required = true)
    private Integer pageNum = 1;

    @ApiModelProperty(value = "页面大小", required = true)
    private Integer pageSize = 10;
}
