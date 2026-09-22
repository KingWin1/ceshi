package com.community.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.community.common.pojo.Menu;

import java.util.List;

/**
 * 菜单Service接口
 */
public interface MenuService extends IService<Menu> {

    /**
     * 根据父级ID查询菜单列表
     * @param parentId 父级菜单ID
     * @return 菜单列表
     */
    List<Menu> listByParentId(Integer parentId);

    /**
     * 查询全部启用菜单（用于权限树渲染）
     * @return 菜单列表
     */
    List<Menu> listAllEnabled();

    /**
     * 根据角色ID查询该角色已分配的菜单列表
     * @param roleId 角色ID
     * @return 菜单列表
     */
    List<Menu> listByRoleId(Integer roleId);
}
