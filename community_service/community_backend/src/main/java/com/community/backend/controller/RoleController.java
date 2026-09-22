package com.community.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.community.backend.dto.RoleDTO;
import com.community.backend.dto.RoleQueryDTO;
import com.community.backend.service.RoleService;
import com.community.backend.vo.RoleVO;
import com.community.common.pojo.Role;
import com.community.common.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理接口
 */
@RestController
@RequestMapping("/api/role")
@Api(tags = "角色管理")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/page")
    @ApiOperation("分页+条件查询角色列表")
    public Result<IPage<Role>> page(RoleQueryDTO queryDTO) {
        IPage<Role> page = roleService.selectRolePage(queryDTO);
        return Result.success(page);
    }

    @GetMapping("/listAll")
    @ApiOperation("查询全部角色信息")
    public Result<List<Role>> listAll() {
        List<Role> list = roleService.listAll();
        return Result.success(list);
    }

    @GetMapping("/getById")
    @ApiOperation("根据角色ID查询角色信息（含已分配菜单权限）")
    public Result<RoleVO> getById(
            @ApiParam(value = "角色ID", required = true) @RequestParam Integer roleId) {
        RoleVO vo = roleService.getRoleDetail(roleId);
        if (vo == null) {
            return Result.error("40001", "角色信息不存在");
        }
        return Result.success(vo);
    }

    @PostMapping("/add")
    @ApiOperation("添加角色并划分权限")
    public Result<String> add(@RequestBody RoleDTO roleDTO) {
        roleService.addRoleWithMenus(roleDTO);
        return Result.success("添加成功");
    }

    @PostMapping("/update")
    @ApiOperation("修改角色信息并划分权限")
    public Result<String> update(@RequestBody RoleDTO roleDTO) {
        roleService.updateRoleWithMenus(roleDTO);
        return Result.success("修改成功");
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除角色（被管理员占用时不可删除）")
    public Result<String> delete(
            @ApiParam(value = "角色ID", required = true) @RequestParam Integer roleId) {
        roleService.deleteRole(roleId);
        return Result.success("删除成功");
    }

    @PostMapping("/batchDelete")
    @ApiOperation("批量删除角色（仅删除未被管理员占用的角色）")
    public Result<String> batchDelete(
            @ApiParam(value = "角色ID数组", required = true) @RequestBody List<Integer> roleIds) {
        String message = roleService.deleteRoleBatch(roleIds);
        return Result.success(message);
    }
}
