package com.community.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 居民关联房间项（楼栋-单元-房间-类型）
 */
@Data
@ApiModel(description = "居民关联房间项")
public class ResidentRoomItemDTO {

    @ApiModelProperty(value = "楼栋ID")
    private Integer buildingId;

    @ApiModelProperty(value = "单元号")
    private String unitNo;

    @ApiModelProperty(value = "房间号")
    private String houseNumber;

    @ApiModelProperty(value = "认证类型：1-业主 2-家属 3-租户")
    private Integer type;
}
