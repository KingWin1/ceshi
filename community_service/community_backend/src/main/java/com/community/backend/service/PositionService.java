package com.community.backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.community.backend.dto.PositionQueryDTO;
import com.community.backend.vo.PositionEmployeeCountVO;
import com.community.common.pojo.Position;

import java.util.List;

/**
 * 岗位Service接口
 */
public interface PositionService extends IService<Position> {

    /**
     * 分页+条件查询岗位列表
     * @param queryDTO 查询参数
     * @return 分页结果
     */
    IPage<Position> selectPositionPage(PositionQueryDTO queryDTO);

    /**
     * 根据ID查询岗位信息
     * @param positionId 岗位ID
     * @return 岗位对象
     */
    Position getPositionById(Integer positionId);

    /**
     * 根据部门ID查询岗位列表
     * @param deptId 部门ID
     * @return 岗位集合
     */
    List<Position> listByDeptId(Integer deptId);

    /**
     * 查询全部岗位信息（下拉框数据源）
     * @return 岗位集合
     */
    List<Position> listAll();

    /**
     * 根据岗位ID统计该岗位在员工表中出现的次数
     * @param positionId 岗位ID
     * @return 员工数量
     */
    int countEmployeeByPositionId(Integer positionId);

    /**
     * 根据岗位ID数组统计每个岗位在员工表中出现的次数
     * @param positionIds 岗位ID数组
     * @return 每个岗位的员工统计
     */
    List<PositionEmployeeCountVO> countEmployeeByPositionIds(List<Integer> positionIds);

    /**
     * 删除岗位信息（联动删除该岗位下的员工数据）
     * @param positionId 岗位ID
     */
    void deletePosition(Integer positionId);

    /**
     * 批量删除岗位信息（联动删除这些岗位下的员工数据）
     * @param positionIds 岗位ID数组
     */
    void deletePositionBatch(List<Integer> positionIds);
}
