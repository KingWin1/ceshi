package com.community.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 角色菜单关联实体类
 */
@Data
@TableName("role_menu")
@ApiModel(description = "角色菜单关联")
public class RoleMenu {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "关联ID")
    private Integer relationId;

    @ApiModelProperty(value = "角色ID")
    private Integer roleId;

    @ApiModelProperty(value = "菜单ID")
    private Integer menuId;
}
