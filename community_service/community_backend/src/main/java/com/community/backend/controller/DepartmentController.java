package com.community.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.community.backend.dto.DepartmentQueryDTO;
import com.community.backend.service.DepartmentService;
import com.community.backend.vo.DeptPositionCountVO;
import com.community.common.pojo.Department;
import com.community.common.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理接口
 */
@RestController
@RequestMapping("/api/department")
@Api(tags = "部门管理")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/page")
    @ApiOperation("分页+条件查询部门列表")
    public Result<IPage<Department>> page(DepartmentQueryDTO queryDTO) {
        IPage<Department> page = departmentService.selectDepartmentPage(queryDTO);
        return Result.success(page);
    }

    @PostMapping("/add")
    @ApiOperation("添加部门信息")
    public Result<String> add(@RequestBody Department department) {
        departmentService.save(department);
        return Result.success("添加成功");
    }

    @GetMapping("/getById")
    @ApiOperation("根据ID查询部门信息")
    public Result<Department> getById(
            @ApiParam(value = "部门ID", required = true) @RequestParam Integer deptId) {
        Department department = departmentService.getDepartmentById(deptId);
        if (department == null) {
            return Result.error("40001", "部门信息不存在");
        }
        return Result.success(department);
    }

    @GetMapping("/listAll")
    @ApiOperation("查询全部部门信息")
    public Result<List<Department>> listAll() {
        List<Department> list = departmentService.listAll();
        return Result.success(list);
    }

    @PostMapping("/update")
    @ApiOperation("修改部门信息")
    public Result<String> update(@RequestBody Department department) {
        departmentService.updateById(department);
        return Result.success("修改成功");
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除部门信息（联动删除该部门下的岗位数据）")
    public Result<String> delete(
            @ApiParam(value = "部门ID", required = true) @RequestParam Integer deptId) {
        departmentService.deleteDepartment(deptId);
        return Result.success("删除成功");
    }

    @PostMapping("/batchDelete")
    @ApiOperation("批量删除部门信息（联动删除这些部门下的岗位数据）")
    public Result<String> batchDelete(
            @ApiParam(value = "部门ID数组", required = true) @RequestBody List<Integer> deptIds) {
        departmentService.deleteDepartmentBatch(deptIds);
        return Result.success("批量删除成功");
    }

    @GetMapping("/countPositionByDeptId")
    @ApiOperation("根据部门ID统计该部门在岗位表中出现的次数")
    public Result<Integer> countPositionByDeptId(
            @ApiParam(value = "部门ID", required = true) @RequestParam Integer deptId) {
        int count = departmentService.countPositionByDeptId(deptId);
        return Result.success(count);
    }

    @PostMapping("/countPositionByDeptIds")
    @ApiOperation("根据部门ID数组统计每个部门在岗位表中出现的次数")
    public Result<List<DeptPositionCountVO>> countPositionByDeptIds(
            @ApiParam(value = "部门ID数组", required = true) @RequestBody List<Integer> deptIds) {
        List<DeptPositionCountVO> list = departmentService.countPositionByDeptIds(deptIds);
        return Result.success(list);
    }
}
