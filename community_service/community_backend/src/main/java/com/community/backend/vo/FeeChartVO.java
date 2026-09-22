package com.community.backend.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 近6个月收费统计图表VO
 */
@Data
@ApiModel(description = "近6个月收费统计")
public class FeeChartVO {

    @ApiModelProperty(value = "月份集合（如 05、06）")
    private List<String> months;

    @ApiModelProperty(value = "各月应收总额")
    private List<BigDecimal> receivable;

    @ApiModelProperty(value = "各月实收金额")
    private List<BigDecimal> received;
}
