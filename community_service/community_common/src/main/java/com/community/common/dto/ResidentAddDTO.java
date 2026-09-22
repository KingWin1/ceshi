package com.community.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 添加居民参数DTO
 */
@Data
@ApiModel(description = "添加居民参数")
public class ResidentAddDTO {

    @ApiModelProperty(value = "姓名", required = true)
    private String name;

    @ApiModelProperty(value = "手机号", required = true)
    private String phone;

    @ApiModelProperty(value = "密码（前台注册时传入，后台新增可为空）")
    private String password;

    @ApiModelProperty(value = "身份证")
    private String idCard;

    @ApiModelProperty(value = "性别：0-男 1-女 2-未知")
    private Integer gender;

    @ApiModelProperty(value = "类型：1-业主 2-家属 3-租户", required = true)
    private Integer type;

    @ApiModelProperty(value = "注册方式：1-App注册 2-后台注册")
    private Integer registerWay;

    @ApiModelProperty(value = "头像地址")
    private String avatarUrl;

    @ApiModelProperty(value = "状态：1-正常 2-停用")
    private Integer status;

    @ApiModelProperty(value = "关联房间列表（楼栋-单元-房间-类型）")
    private List<ResidentRoomItemDTO> rooms;
}
