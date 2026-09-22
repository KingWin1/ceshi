package com.community.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.backend.dto.EmployeeQueryDTO;
import com.community.backend.mapper.EmployeeMapper;
import com.community.backend.service.EmployeeService;
import com.community.common.pojo.Employee;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 员工Service实现类
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    @Override
    public IPage<Employee> selectEmployeePage(EmployeeQueryDTO queryDTO) {
        Page<Employee> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        // 关键词：姓名或手机号模糊匹配（OR 语义）
        if (StringUtils.hasText(queryDTO.getKeyword())) {
            String keyword = queryDTO.getKeyword();
            wrapper.and(w -> w.like(Employee::getEmployeeName, keyword)
                    .or()
                    .like(Employee::getPhone, keyword));
        }
        // 姓名模糊查询
        if (StringUtils.hasText(queryDTO.getEmployeeName())) {
            wrapper.like(Employee::getEmployeeName, queryDTO.getEmployeeName());
        }
        // 手机号查询
        if (StringUtils.hasText(queryDTO.getPhone())) {
            wrapper.eq(Employee::getPhone, queryDTO.getPhone());
        }
        // 部门ID查询
        if (queryDTO.getDeptId() != null) {
            wrapper.eq(Employee::getDeptId, queryDTO.getDeptId());
        }
        // 岗位ID查询
        if (queryDTO.getPositionId() != null) {
            wrapper.eq(Employee::getPositionId, queryDTO.getPositionId());
        }
        // 状态查询
        if (queryDTO.getStatus() != null) {
            wrapper.eq(Employee::getStatus, queryDTO.getStatus());
        }
        wrapper.orderByAsc(Employee::getEmployeeId);

        return page(page, wrapper);
    }

    @Override
    public List<Employee> listAll() {
        return list();
    }

    @Override
    public Employee getEmployeeById(Integer employeeId) {
        return getById(employeeId);
    }
}
