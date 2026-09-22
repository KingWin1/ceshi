package com.community.backend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 报修记录查询参数DTO
 */
@Data
@ApiModel(description = "报修记录查询参数")
public class RepairRecordQueryDTO {

    @ApiModelProperty(value = "报修单号/联系人/电话/地址/备注（模糊查询）")
    private String keyword;

    @ApiModelProperty(value = "报修类型ID")
    private Integer repairTypeId;

    @ApiModelProperty(value = "状态：1-待派单 2-已派单 3-处理中 4-已完成 5-已取消")
    private Integer status;

    @ApiModelProperty(value = "当前页码", required = true)
    private Integer pageNum = 1;

    @ApiModelProperty(value = "页面大小", required = true)
    private Integer pageSize = 10;
}
