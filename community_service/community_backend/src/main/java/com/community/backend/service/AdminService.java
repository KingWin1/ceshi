package com.community.backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.community.backend.dto.AdminQueryDTO;
import com.community.common.pojo.Admin;

import java.util.Map;

/**
 * 管理员Service接口
 */
public interface AdminService extends IService<Admin> {

    /**
     * 管理员登录
     * @param username 用户名
     * @param password 密码
     * @return 登录结果（管理员信息+token）
     */
    Map<String, Object> login(String username, String password);

    /**
     * 分页+条件查询管理员列表
     * @param queryDTO 查询参数
     * @return 分页结果
     */
    IPage<Admin> selectAdminPage(AdminQueryDTO queryDTO);

    /**
     * 根据ID查询管理员信息
     * @param adminId 管理员ID
     * @return 管理员对象
     */
    Admin getAdminById(Integer adminId);

    /**
     * 修改管理员状态
     * @param adminId 管理员ID
     * @param status 状态
     */
    void updateStatus(Integer adminId, Integer status);

    /**
     * 修改管理员密码
     * @param adminId 管理员ID
     * @param newPassword 新密码
     */
    void updatePassword(Integer adminId, String newPassword);

    /**
     * 根据管理员ID重置密码（重置为默认密码 123456）
     * @param adminId 管理员ID
     */
    void resetPassword(Integer adminId);
}
