package com.community.backend.vo;

import com.community.common.pojo.Store;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 门店详情VO（多表查询结果：门店 + 分类名称）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "门店详情")
public class StoreVO extends Store {

    @ApiModelProperty(value = "门店分类名称")
    private String categoryName;
}
