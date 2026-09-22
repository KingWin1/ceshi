package com.community.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.backend.dto.RoleDTO;
import com.community.backend.dto.RoleQueryDTO;
import com.community.backend.mapper.AdminMapper;
import com.community.backend.mapper.RoleMapper;
import com.community.backend.mapper.RoleMenuMapper;
import com.community.backend.service.RoleService;
import com.community.backend.vo.RoleVO;
import com.community.common.exception.BusinessException;
import com.community.common.pojo.Admin;
import com.community.common.pojo.Role;
import com.community.common.pojo.RoleMenu;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 角色Service实现类
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public IPage<Role> selectRolePage(RoleQueryDTO queryDTO) {
        Page<Role> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(queryDTO.getRoleName())) {
            wrapper.like(Role::getRoleName, queryDTO.getRoleName());
        }
        wrapper.orderByDesc(Role::getCreateTime);

        return page(page, wrapper);
    }

    @Override
    public List<Role> listAll() {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Role::getRoleId);
        return list(wrapper);
    }

    @Override
    public RoleVO getRoleDetail(Integer roleId) {
        Role role = getById(roleId);
        if (role == null) {
            return null;
        }
        RoleVO vo = new RoleVO();
        BeanUtils.copyProperties(role, vo);
        // 查询该角色已分配的菜单权限ID集合
        LambdaQueryWrapper<RoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleMenu::getRoleId, roleId);
        List<RoleMenu> roleMenus = roleMenuMapper.selectList(wrapper);
        List<Integer> menuIds = new ArrayList<>();
        for (RoleMenu roleMenu : roleMenus) {
            menuIds.add(roleMenu.getMenuId());
        }
        vo.setMenuIds(menuIds);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addRoleWithMenus(RoleDTO roleDTO) {
        // 1. 添加角色信息
        Role role = new Role();
        BeanUtils.copyProperties(roleDTO, role);
        role.setRoleId(null);
        save(role);

        // 2. 批量添加角色菜单关联信息
        saveRoleMenus(role.getRoleId(), roleDTO.getMenuIds());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRoleWithMenus(RoleDTO roleDTO) {
        if (roleDTO.getRoleId() == null) {
            throw new BusinessException("角色ID不能为空");
        }
        // 1. 修改角色信息
        Role role = new Role();
        BeanUtils.copyProperties(roleDTO, role);
        updateById(role);

        // 2. 删除原有角色菜单关联
        LambdaQueryWrapper<RoleMenu> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(RoleMenu::getRoleId, role.getRoleId());
        roleMenuMapper.delete(deleteWrapper);

        // 3. 重新添加新的角色菜单关联
        saveRoleMenus(role.getRoleId(), roleDTO.getMenuIds());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Integer roleId) {
        // 1. 检查该角色是否被管理员占用，占用时不可删除
        Long count = adminMapper.selectCount(new LambdaQueryWrapper<Admin>()
                .eq(Admin::getRoleId, roleId));
        if (count > 0) {
            throw new BusinessException("该角色已被管理员使用，无法删除");
        }

        // 2. 删除角色信息
        removeById(roleId);

        // 3. 删除角色菜单关联信息
        roleMenuMapper.delete(new LambdaQueryWrapper<RoleMenu>()
                .eq(RoleMenu::getRoleId, roleId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String deleteRoleBatch(List<Integer> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return "批量删除成功";
        }

        // 1. 查询被管理员占用的角色ID
        Set<Integer> occupiedIds = new HashSet<>();
        List<Admin> occupiedAdmins = adminMapper.selectList(new LambdaQueryWrapper<Admin>()
                .select(Admin::getRoleId)
                .in(Admin::getRoleId, roleIds));
        for (Admin admin : occupiedAdmins) {
            occupiedIds.add(admin.getRoleId());
        }

        // 2. 仅删除未被占用的角色
        List<Integer> deletableIds = new ArrayList<>();
        for (Integer id : roleIds) {
            if (!occupiedIds.contains(id)) {
                deletableIds.add(id);
            }
        }
        if (deletableIds.isEmpty()) {
            throw new BusinessException("所选角色均已被管理员使用，无法删除");
        }
        removeByIds(deletableIds);
        roleMenuMapper.delete(new LambdaQueryWrapper<RoleMenu>()
                .in(RoleMenu::getRoleId, deletableIds));

        // 3. 提示被占用未删除的角色名称
        if (occupiedIds.isEmpty()) {
            return "批量删除成功";
        }
        List<Role> occupiedRoles = listByIds(occupiedIds);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < occupiedRoles.size(); i++) {
            if (i > 0) {
                sb.append("、");
            }
            sb.append(occupiedRoles.get(i).getRoleName());
        }
        return "以下角色已被管理员使用未删除：" + sb.toString();
    }

    /**
     * 批量保存角色菜单关联
     * @param roleId 角色ID
     * @param menuIds 菜单ID集合
     */
    private void saveRoleMenus(Integer roleId, List<Integer> menuIds) {
        if (menuIds == null || menuIds.isEmpty()) {
            return;
        }
        for (Integer menuId : menuIds) {
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleMenuMapper.insert(roleMenu);
        }
    }
}
