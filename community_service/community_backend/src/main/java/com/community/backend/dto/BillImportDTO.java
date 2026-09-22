package com.community.backend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 账单导入参数DTO
 */
@Data
@ApiModel(description = "账单导入参数")
public class BillImportDTO {

    @ApiModelProperty(value = "是否覆盖重复账单")
    private Boolean overwrite = false;
}
