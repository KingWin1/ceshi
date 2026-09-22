package com.community.backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.community.backend.dto.RoleDTO;
import com.community.backend.dto.RoleQueryDTO;
import com.community.backend.vo.RoleVO;
import com.community.common.pojo.Role;

import java.util.List;

/**
 * 角色Service接口
 */
public interface RoleService extends IService<Role> {

    /**
     * 分页+条件查询角色列表
     * @param queryDTO 查询参数
     * @return 分页结果
     */
    IPage<Role> selectRolePage(RoleQueryDTO queryDTO);

    /**
     * 查询全部角色信息（下拉框数据源）
     * @return 角色集合
     */
    List<Role> listAll();

    /**
     * 根据角色ID查询角色详情（含已分配菜单权限ID集合）
     * @param roleId 角色ID
     * @return 角色详情VO
     */
    RoleVO getRoleDetail(Integer roleId);

    /**
     * 添加角色并划分权限
     * @param roleDTO 角色信息（含菜单权限ID集合）
     */
    void addRoleWithMenus(RoleDTO roleDTO);

    /**
     * 修改角色信息并重新划分权限
     * @param roleDTO 角色信息（含菜单权限ID集合）
     */
    void updateRoleWithMenus(RoleDTO roleDTO);

    /**
     * 根据角色ID删除角色（被管理员占用时不可删除）
     * @param roleId 角色ID
     */
    void deleteRole(Integer roleId);

    /**
     * 批量删除角色（仅删除未被管理员占用的角色）
     * @param roleIds 角色ID数组
     * @return 删除结果提示信息
     */
    String deleteRoleBatch(List<Integer> roleIds);
}
