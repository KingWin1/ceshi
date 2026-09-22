package com.community.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.backend.vo.DeptPositionCountVO;
import com.community.common.pojo.Position;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 岗位Mapper接口
 */
@Mapper
public interface PositionMapper extends BaseMapper<Position> {

    /**
     * 根据部门ID统计该部门在岗位表中出现的次数
     * @param deptId 部门ID
     * @return 岗位数量
     */
    int countByDeptId(@Param("deptId") Integer deptId);

    /**
     * 根据部门ID数组统计每个部门在岗位表中出现的次数（无岗位记录的部门统计为0）
     * @param deptIds 部门ID数组
     * @return 每个部门的岗位统计
     */
    List<DeptPositionCountVO> countByDeptIds(@Param("deptIds") List<Integer> deptIds);

    /**
     * 根据部门ID删除岗位数据
     * @param deptId 部门ID
     */
    void deleteByDeptId(@Param("deptId") Integer deptId);

    /**
     * 根据部门ID数组批量删除岗位数据
     * @param deptIds 部门ID数组
     */
    void deleteByDeptIds(@Param("deptIds") List<Integer> deptIds);

    /**
     * 根据部门ID数组批量查询岗位数据
     * @param deptIds 部门ID数组
     * @return 岗位信息集合
     */
    List<Position> selectListByDeptIds(@Param("deptIds") List<Integer> deptIds);
}
