package com.community.backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.community.backend.dto.EmployeeQueryDTO;
import com.community.common.pojo.Employee;

import java.util.List;

/**
 * 员工Service接口
 */
public interface EmployeeService extends IService<Employee> {

    /**
     * 分页+条件查询员工列表
     * @param queryDTO 查询参数
     * @return 分页结果
     */
    IPage<Employee> selectEmployeePage(EmployeeQueryDTO queryDTO);

    /**
     * 查询全部员工信息
     * @return 员工信息集合
     */
    List<Employee> listAll();

    /**
     * 根据ID查询员工信息
     * @param employeeId 员工ID
     * @return 员工对象
     */
    Employee getEmployeeById(Integer employeeId);
}
