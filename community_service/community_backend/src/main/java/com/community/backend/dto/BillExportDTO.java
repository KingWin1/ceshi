package com.community.backend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 账单导出参数DTO
 */
@Data
@ApiModel(description = "账单导出参数")
public class BillExportDTO {

    @ApiModelProperty(value = "导出范围：1-当前页 2-全部数据 3-按筛选条件", required = true)
    private Integer exportRange;

    @ApiModelProperty(value = "导出字段列表", required = true)
    private List<String> exportFields;

    @ApiModelProperty(value = "文件格式：1-Excel 2-CSV", required = true)
    private Integer fileFormat;

    @ApiModelProperty(value = "查询参数（导出范围为3时使用）")
    private BillQueryDTO queryDTO;

    @ApiModelProperty(value = "当前页码（导出范围为1时使用）")
    private Integer pageNum;

    @ApiModelProperty(value = "页面大小（导出范围为1时使用）")
    private Integer pageSize;
}
