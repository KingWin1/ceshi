package com.community.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.backend.dto.AdminQueryDTO;
import com.community.backend.mapper.AdminMapper;
import com.community.backend.mapper.RoleMenuMapper;
import com.community.backend.service.AdminService;
import com.community.common.pojo.Admin;
import com.community.common.pojo.RoleMenu;
import com.community.common.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员Service实现类
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    /** 重置密码时的默认密码 */
    private static final String DEFAULT_PASSWORD = "123456";

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Override
    public Map<String, Object> login(String username, String password) {
        // 根据用户名查询管理员
        Admin admin = getOne(new LambdaQueryWrapper<Admin>()
                .eq(Admin::getUsername, username));

        if (admin == null) {
            throw new RuntimeException("用户名不存在");
        }

        // 校验密码
        if (!admin.getPassword().equals(password)) {
            throw new RuntimeException("密码错误");
        }

        // 校验状态
        if (admin.getStatus() != 1) {
            throw new RuntimeException("账号已被禁用");
        }

        // 更新最后登录时间
        admin.setLastLoginTime(new Date());
        updateById(admin);

        // 生成Token
        String token = JwtUtil.generateToken(String.valueOf(admin.getAdminId()));

        // 查询该管理员角色已分配的菜单权限ID集合（用于前端权限控制）
        List<Integer> menuIds = new ArrayList<>();
        if (admin.getRoleId() != null) {
            List<RoleMenu> roleMenus = roleMenuMapper.selectList(new LambdaQueryWrapper<RoleMenu>()
                    .eq(RoleMenu::getRoleId, admin.getRoleId()));
            for (RoleMenu roleMenu : roleMenus) {
                menuIds.add(roleMenu.getMenuId());
            }
        }

        // 封装返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("admin", admin);
        result.put("token", token);
        result.put("menuIds", menuIds);
        return result;
    }

    @Override
    public IPage<Admin> selectAdminPage(AdminQueryDTO queryDTO) {
        Page<Admin> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        // 关键词：用户名/姓名/手机号 模糊匹配（OR 语义）
        if (StringUtils.hasText(queryDTO.getKeyword())) {
            String keyword = queryDTO.getKeyword();
            wrapper.and(w -> w.like(Admin::getUsername, keyword)
                    .or()
                    .like(Admin::getName, keyword)
                    .or()
                    .like(Admin::getPhone, keyword));
        }
        // 用户名模糊查询
        if (StringUtils.hasText(queryDTO.getUsername())) {
            wrapper.like(Admin::getUsername, queryDTO.getUsername());
        }
        // 姓名模糊查询
        if (StringUtils.hasText(queryDTO.getName())) {
            wrapper.like(Admin::getName, queryDTO.getName());
        }
        // 手机号查询
        if (StringUtils.hasText(queryDTO.getPhone())) {
            wrapper.eq(Admin::getPhone, queryDTO.getPhone());
        }
        // 角色ID查询
        if (queryDTO.getRoleId() != null) {
            wrapper.eq(Admin::getRoleId, queryDTO.getRoleId());
        }
        // 状态查询
        if (queryDTO.getStatus() != null) {
            wrapper.eq(Admin::getStatus, queryDTO.getStatus());
        }
        wrapper.orderByAsc(Admin::getAdminId);

        return page(page, wrapper);
    }

    @Override
    public Admin getAdminById(Integer adminId) {
        return getById(adminId);
    }

    @Override
    public void updateStatus(Integer adminId, Integer status) {
        LambdaUpdateWrapper<Admin> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Admin::getAdminId, adminId)
               .set(Admin::getStatus, status);
        update(wrapper);
    }

    @Override
    public void updatePassword(Integer adminId, String newPassword) {
        LambdaUpdateWrapper<Admin> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Admin::getAdminId, adminId)
               .set(Admin::getPassword, newPassword);
        update(wrapper);
    }

    @Override
    public void resetPassword(Integer adminId) {
        // 重置为默认密码
        updatePassword(adminId, DEFAULT_PASSWORD);
    }
}
