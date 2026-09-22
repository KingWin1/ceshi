package com.community.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.community.backend.dto.EmployeeQueryDTO;
import com.community.backend.service.EmployeeService;
import com.community.common.pojo.Employee;
import com.community.common.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 员工管理接口
 */
@RestController
@RequestMapping("/api/employee")
@Api(tags = "员工管理")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/page")
    @ApiOperation("分页+条件查询员工列表")
    public Result<IPage<Employee>> page(EmployeeQueryDTO queryDTO) {
        IPage<Employee> page = employeeService.selectEmployeePage(queryDTO);
        return Result.success(page);
    }

    @PostMapping("/add")
    @ApiOperation("添加员工信息")
    public Result<String> add(@RequestBody Employee employee) {
        employeeService.save(employee);
        return Result.success("添加成功");
    }

    @GetMapping("/getById")
    @ApiOperation("根据ID查询员工信息")
    public Result<Employee> getById(
            @ApiParam(value = "员工ID", required = true) @RequestParam Integer employeeId) {
        Employee employee = employeeService.getEmployeeById(employeeId);
        if (employee == null) {
            return Result.error("40001", "员工信息不存在");
        }
        return Result.success(employee);
    }

    @PostMapping("/update")
    @ApiOperation("修改员工信息")
    public Result<String> update(@RequestBody Employee employee) {
        employeeService.updateById(employee);
        return Result.success("修改成功");
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除员工信息")
    public Result<String> delete(
            @ApiParam(value = "员工ID", required = true) @RequestParam Integer employeeId) {
        employeeService.removeById(employeeId);
        return Result.success("删除成功");
    }

    @PostMapping("/batchDelete")
    @ApiOperation("批量删除员工信息")
    public Result<String> batchDelete(
            @ApiParam(value = "员工ID数组", required = true) @RequestBody List<Integer> employeeIds) {
        employeeService.removeByIds(employeeIds);
        return Result.success("批量删除成功");
    }

    @GetMapping("/listAll")
    @ApiOperation("查询全部员工信息")
    public Result<List<Employee>> listAll() {
        List<Employee> list = employeeService.listAll();
        return Result.success(list);
    }
}
