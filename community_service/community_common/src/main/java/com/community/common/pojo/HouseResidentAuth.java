package com.community.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 房屋居民认证实体类
 */
@Data
@TableName("house_resident_auth")
@ApiModel(description = "房屋居民认证")
public class HouseResidentAuth {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "认证ID")
    private Integer authId;

    @ApiModelProperty(value = "房屋ID")
    private Integer houseId;

    @ApiModelProperty(value = "居民ID")
    private Integer residentId;

    @ApiModelProperty(value = "关系类型：1-业主 2-家属 3-租户")
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

    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
