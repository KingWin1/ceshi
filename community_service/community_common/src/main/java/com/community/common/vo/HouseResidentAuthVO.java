package com.community.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 房屋居民认证信息VO（多表查询结果）
 */
@Data
@ApiModel(description = "认证信息")
public class HouseResidentAuthVO {

    @ApiModelProperty(value = "认证ID")
    private Integer authId;

    @ApiModelProperty(value = "居民ID")
    private Integer residentId;

    @ApiModelProperty(value = "楼栋名称")
    private String buildingName;

    @ApiModelProperty(value = "单元号")
    private String unitNo;

    @ApiModelProperty(value = "房间号")
    private String houseNumber;

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "身份证号")
    private String idCard;

    @ApiModelProperty(value = "电话")
    private String phone;

    @ApiModelProperty(value = "类型：1-业主 2-家属 3-租户")
    private Integer type;

    @ApiModelProperty(value = "认证材料图片地址")
    private String authMaterialUrl;

    @ApiModelProperty(value = "审核状态：1-待审核 2-已通过 3-已拒绝")
    private Integer auditStatus;

    @ApiModelProperty(value = "审核备注")
    private String auditRemark;

    @ApiModelProperty(value = "管理员用户名")
    private String adminUsername;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
