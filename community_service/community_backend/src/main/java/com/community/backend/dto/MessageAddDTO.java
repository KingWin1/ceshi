package com.community.backend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 添加消息参数DTO
 */
@Data
@ApiModel(description = "添加消息参数")
public class MessageAddDTO {

    @ApiModelProperty(value = "账单ID", required = true)
    private Integer billId;

    @ApiModelProperty(value = "居民ID", required = true)
    private Integer residentId;

    @ApiModelProperty(value = "消息标题", required = true)
    private String title;

    @ApiModelProperty(value = "消息内容", required = true)
    private String content;
}
