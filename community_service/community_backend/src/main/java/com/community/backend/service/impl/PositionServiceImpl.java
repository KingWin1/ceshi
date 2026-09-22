package com.community.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.backend.dto.PositionQueryDTO;
import com.community.backend.mapper.EmployeeMapper;
import com.community.backend.mapper.PositionMapper;
import com.community.backend.service.PositionService;
import com.community.backend.vo.PositionEmployeeCountVO;
import com.community.common.pojo.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * 岗位Service实现类
 */
@Service
public class PositionServiceImpl extends ServiceImpl<PositionMapper, Position> implements PositionService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    public IPage<Position> selectPositionPage(PositionQueryDTO queryDTO) {
        Page<Position> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<Position> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(queryDTO.getPositionName())) {
            wrapper.like(Position::getPositionName, queryDTO.getPositionName());
        }
        wrapper.orderByAsc(Position::getSort);

        return page(page, wrapper);
    }

    @Override
    public Position getPositionById(Integer positionId) {
        return getById(positionId);
    }

    @Override
    public List<Position> listByDeptId(Integer deptId) {
        LambdaQueryWrapper<Position> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Position::getDeptId, deptId);
        wrapper.orderByAsc(Position::getSort);
        return list(wrapper);
    }

    @Override
    public List<Position> listAll() {
        LambdaQueryWrapper<Position> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Position::getSort);
        return list(wrapper);
    }

    @Override
    public int countEmployeeByPositionId(Integer positionId) {
        return employeeMapper.countByPositionId(positionId);
    }

    @Override
    public List<PositionEmployeeCountVO> countEmployeeByPositionIds(List<Integer> positionIds) {
        if (positionIds == null || positionIds.isEmpty()) {
            return Collections.emptyList();
        }
        return employeeMapper.countByPositionIds(positionIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePosition(Integer positionId) {
        // 先删除岗位信息，再联动删除该岗位下的员工数据
        removeById(positionId);
        employeeMapper.deleteByPositionId(positionId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePositionBatch(List<Integer> positionIds) {
        if (positionIds == null || positionIds.isEmpty()) {
            return;
        }
        // 先联动删除这些岗位下的员工数据，再批量删除岗位信息
        employeeMapper.deleteByPositionIds(positionIds);
        removeByIds(positionIds);
    }
}
