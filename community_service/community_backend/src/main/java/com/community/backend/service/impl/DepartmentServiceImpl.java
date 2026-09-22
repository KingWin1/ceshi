package com.community.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.backend.dto.DepartmentQueryDTO;
import com.community.backend.mapper.DepartmentMapper;
import com.community.backend.mapper.PositionMapper;
import com.community.backend.service.DepartmentService;
import com.community.backend.vo.DeptPositionCountVO;
import com.community.common.pojo.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 部门Service实现类
 */
@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {

    @Autowired
    private PositionMapper positionMapper;

    @Override
    public IPage<Department> selectDepartmentPage(DepartmentQueryDTO queryDTO) {
        Page<Department> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(queryDTO.getDeptName())) {
            wrapper.like(Department::getDeptName, queryDTO.getDeptName());
        }
        wrapper.orderByAsc(Department::getSort);

        return page(page, wrapper);
    }

    @Override
    public Department getDepartmentById(Integer deptId) {
        return getById(deptId);
    }

    @Override
    public List<Department> listAll() {
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Department::getSort);
        return list(wrapper);
    }

    @Override
    public int countPositionByDeptId(Integer deptId) {
        return positionMapper.countByDeptId(deptId);
    }

    @Override
    public List<DeptPositionCountVO> countPositionByDeptIds(List<Integer> deptIds) {
        if (deptIds == null || deptIds.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return positionMapper.countByDeptIds(deptIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDepartment(Integer deptId) {
        // 先删除部门信息，再联动删除该部门下的岗位数据
        removeById(deptId);
        positionMapper.deleteByDeptId(deptId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDepartmentBatch(List<Integer> deptIds) {
        if (deptIds == null || deptIds.isEmpty()) {
            return;
        }
        // 先批量删除这些部门下的岗位数据，再批量删除部门信息
        positionMapper.deleteByDeptIds(deptIds);
        removeByIds(deptIds);
    }
}
