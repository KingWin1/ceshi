package com.community.backend.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 服务工单统计图表VO（报修单/投诉建议 按状态堆叠）
 */
@Data
@ApiModel(description = "服务工单统计")
public class WorkOrderChartVO {

    @ApiModelProperty(value = "分类（报修单、投诉建议）")
    private List<String> categories;

    @ApiModelProperty(value = "各分类待处理数量")
    private List<Integer> pending;

    @ApiModelProperty(value = "各分类已派单数量")
    private List<Integer> dispatched;

    @ApiModelProperty(value = "各分类处理中数量")
    private List<Integer> handling;
}
