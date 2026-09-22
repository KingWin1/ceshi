package com.community.backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.community.backend.dto.DepartmentQueryDTO;
import com.community.backend.vo.DeptPositionCountVO;
import com.community.common.pojo.Department;

import java.util.List;

/**
 * 部门Service接口
 */
public interface DepartmentService extends IService<Department> {

    /**
     * 分页+条件查询部门列表
     * @param queryDTO 查询参数
     * @return 分页结果
     */
    IPage<Department> selectDepartmentPage(DepartmentQueryDTO queryDTO);

    /**
     * 根据ID查询部门信息
     * @param deptId 部门ID
     * @return 部门对象
     */
    Department getDepartmentById(Integer deptId);

    /**
     * 查询全部部门信息（下拉框数据源）
     * @return 部门集合
     */
    List<Department> listAll();

    /**
     * 根据部门ID统计该部门在岗位表中出现的次数
     * @param deptId 部门ID
     * @return 岗位数量
     */
    int countPositionByDeptId(Integer deptId);

    /**
     * 根据部门ID数组统计每个部门在岗位表中出现的次数
     * @param deptIds 部门ID数组
     * @return 每个部门的岗位统计
     */
    List<DeptPositionCountVO> countPositionByDeptIds(List<Integer> deptIds);

    /**
     * 删除部门信息（联动删除该部门下的岗位数据）
     * @param deptId 部门ID
     */
    void deleteDepartment(Integer deptId);

    /**
     * 批量删除部门信息（联动删除这些部门下的岗位数据）
     * @param deptIds 部门ID数组
     */
    void deleteDepartmentBatch(List<Integer> deptIds);
}
