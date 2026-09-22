package com.community.backend.dto;

import com.community.common.pojo.Role;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 角色新增/修改DTO（角色信息 + 分配的菜单权限ID集合）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "角色新增/修改参数")
public class RoleDTO extends Role {

    @ApiModelProperty(value = "菜单权限ID集合")
    private List<Integer> menuIds;
}
