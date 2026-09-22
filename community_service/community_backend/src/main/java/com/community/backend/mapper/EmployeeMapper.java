package com.community.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.backend.vo.PositionEmployeeCountVO;
import com.community.common.pojo.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 员工Mapper接口
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

    /**
     * 根据岗位ID统计该岗位在员工表中出现的次数
     * @param positionId 岗位ID
     * @return 员工数量
     */
    int countByPositionId(@Param("positionId") Integer positionId);

    /**
     * 根据岗位ID数组统计每个岗位在员工表中出现的次数（无员工记录的岗位统计为0）
     * @param positionIds 岗位ID数组
     * @return 每个岗位的员工统计
     */
    List<PositionEmployeeCountVO> countByPositionIds(@Param("positionIds") List<Integer> positionIds);

    /**
     * 根据岗位ID删除员工数据
     * @param positionId 岗位ID
     */
    void deleteByPositionId(@Param("positionId") Integer positionId);

    /**
     * 根据岗位ID数组批量删除员工数据
     * @param positionIds 岗位ID数组
     */
    void deleteByPositionIds(@Param("positionIds") List<Integer> positionIds);

    /**
     * 根据岗位ID数组批量查询员工数据
     * @param positionIds 岗位ID数组
     * @return 员工信息集合
     */
    List<Employee> selectListByPositionIds(@Param("positionIds") List<Integer> positionIds);
}
