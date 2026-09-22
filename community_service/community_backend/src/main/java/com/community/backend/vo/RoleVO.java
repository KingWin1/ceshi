package com.community.backend.vo;

import com.community.common.pojo.Role;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 角色详情VO（角色信息 + 已分配的菜单权限ID集合）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "角色详情")
public class RoleVO extends Role {

    @ApiModelProperty(value = "已分配的菜单权限ID集合")
    private List<Integer> menuIds;
}
