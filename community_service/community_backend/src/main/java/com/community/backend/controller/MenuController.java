package com.community.backend.controller;

import com.community.backend.service.MenuService;
import com.community.common.pojo.Menu;
import com.community.common.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 菜单接口
 */
@RestController
@RequestMapping("/api/menu")
@Api(tags = "菜单管理")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping("/listByParentId")
    @ApiOperation("根据父级ID查询菜单列表")
    public Result<List<Menu>> listByParentId(
            @ApiParam(value = "父级菜单ID") @RequestParam Integer parentId) {
        List<Menu> list = menuService.listByParentId(parentId);
        return Result.success(list);
    }

    @GetMapping("/listAll")
    @ApiOperation("查询全部启用菜单（用于权限树）")
    public Result<List<Menu>> listAll() {
        List<Menu> list = menuService.listAllEnabled();
        return Result.success(list);
    }

    @GetMapping("/listByRoleId")
    @ApiOperation("根据角色ID查询已分配的菜单列表")
    public Result<List<Menu>> listByRoleId(
            @ApiParam(value = "角色ID", required = true) @RequestParam Integer roleId) {
        List<Menu> list = menuService.listByRoleId(roleId);
        return Result.success(list);
    }
}
