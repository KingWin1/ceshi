package com.community.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.backend.mapper.MenuMapper;
import com.community.backend.mapper.RoleMenuMapper;
import com.community.backend.service.MenuService;
import com.community.common.pojo.Menu;
import com.community.common.pojo.RoleMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单Service实现类
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Override
    public List<Menu> listByParentId(Integer parentId) {
        return list(new LambdaQueryWrapper<Menu>()
                .eq(Menu::getParentId, parentId)
                .orderByAsc(Menu::getSort));
    }

    @Override
    public List<Menu> listAllEnabled() {
        return list(new LambdaQueryWrapper<Menu>()
                .eq(Menu::getEnabled, 1)
                .orderByAsc(Menu::getSort)
                .orderByAsc(Menu::getMenuId));
    }

    @Override
    public List<Menu> listByRoleId(Integer roleId) {
        // 查询角色关联的菜单ID集合
        LambdaQueryWrapper<RoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleMenu::getRoleId, roleId);
        List<RoleMenu> roleMenus = roleMenuMapper.selectList(wrapper);
        List<Integer> menuIds = new ArrayList<>();
        for (RoleMenu roleMenu : roleMenus) {
            menuIds.add(roleMenu.getMenuId());
        }
        if (menuIds.isEmpty()) {
            return new ArrayList<>();
        }
        // 查询菜单信息
        return list(new LambdaQueryWrapper<Menu>()
                .in(Menu::getMenuId, menuIds)
                .orderByAsc(Menu::getSort)
                .orderByAsc(Menu::getMenuId));
    }
}
