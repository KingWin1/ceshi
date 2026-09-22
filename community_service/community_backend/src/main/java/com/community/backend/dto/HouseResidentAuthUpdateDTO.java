package com.community.backend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 修改认证信息参数DTO
 */
@Data
@ApiModel(description = "修改认证信息参数")
public class HouseResidentAuthUpdateDTO {

    @ApiModelProperty(value = "认证ID", required = true)
    private Integer authId;

    @ApiModelProperty(value = "房屋ID", required = true)
    private Integer houseId;

    @ApiModelProperty(value = "居民ID", required = true)
    private Integer residentId;

    @ApiModelProperty(value = "关系类型：1-业主 2-家属 3-租户", required = true)
    private Integer type;

    @ApiModelProperty(value = "认证材料地址")
    private String authMaterialUrl;

    @ApiModelProperty(value = "审核状态：1-待审核 2-已通过 3-已拒绝")
    private Integer auditStatus;

    @ApiModelProperty(value = "审核备注")
    private String auditRemark;

    @ApiModelProperty(value = "管理员ID")
    private Integer adminId;

    @ApiModelProperty(value = "主房屋：1-是 2-否")
    private Integer isMain;
}
