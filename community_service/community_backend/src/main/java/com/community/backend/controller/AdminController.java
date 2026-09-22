package com.community.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.community.backend.dto.AdminQueryDTO;
import com.community.backend.dto.LoginDTO;
import com.community.backend.service.AdminService;
import com.community.common.pojo.Admin;
import com.community.common.utils.RedisUtil;
import com.community.common.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 管理员接口
 */
@RestController
@RequestMapping("/api/admin")
@Api(tags = "管理员管理")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private RedisUtil redisUtil;

    @PostMapping("/login")
    @ApiOperation("管理员登录")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO loginDTO) {
        // 从Redis中获取验证码
        String redisKey = "captcha:" + loginDTO.getUuid();
        String captchaCode = (String) redisUtil.get(redisKey);

        // 验证码校验
        if (captchaCode == null) {
            return Result.error("40001", "验证码已过期，请重新获取");
        }
        if (!captchaCode.equalsIgnoreCase(loginDTO.getCode())) {
            return Result.error("40002", "验证码错误");
        }

        // 验证码用完即删
        redisUtil.delete(redisKey);

        // 执行登录
        try {
            Map<String, Object> data = adminService.login(loginDTO.getUsername(), loginDTO.getPassword());
            return Result.success(data);
        } catch (RuntimeException e) {
            return Result.error("40003", e.getMessage());
        }
    }

    @GetMapping("/page")
    @ApiOperation("分页+条件查询管理员列表")
    public Result<IPage<Admin>> page(AdminQueryDTO queryDTO) {
        IPage<Admin> page = adminService.selectAdminPage(queryDTO);
        return Result.success(page);
    }

    @PostMapping("/add")
    @ApiOperation("添加管理员信息")
    public Result<String> add(@RequestBody Admin admin) {
        adminService.save(admin);
        return Result.success("添加成功");
    }

    @GetMapping("/getById")
    @ApiOperation("根据ID查询管理员信息")
    public Result<Admin> getById(
            @ApiParam(value = "管理员ID", required = true) @RequestParam Integer adminId) {
        Admin admin = adminService.getAdminById(adminId);
        if (admin == null) {
            return Result.error("40001", "管理员信息不存在");
        }
        return Result.success(admin);
    }

    @PostMapping("/update")
    @ApiOperation("修改管理员信息")
    public Result<String> update(@RequestBody Admin admin) {
        adminService.updateById(admin);
        return Result.success("修改成功");
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除管理员信息")
    public Result<String> delete(
            @ApiParam(value = "管理员ID", required = true) @RequestParam Integer adminId) {
        adminService.removeById(adminId);
        return Result.success("删除成功");
    }

    @PostMapping("/batchDelete")
    @ApiOperation("批量删除管理员信息")
    public Result<String> batchDelete(
            @ApiParam(value = "管理员ID数组", required = true) @RequestBody List<Integer> adminIds) {
        adminService.removeByIds(adminIds);
        return Result.success("批量删除成功");
    }

    @PostMapping("/updateStatus")
    @ApiOperation("修改管理员状态")
    public Result<String> updateStatus(
            @ApiParam(value = "管理员ID", required = true) @RequestParam Integer adminId,
            @ApiParam(value = "状态", required = true) @RequestParam Integer status) {
        adminService.updateStatus(adminId, status);
        return Result.success("修改状态成功");
    }

    @PostMapping("/updatePassword")
    @ApiOperation("修改管理员密码")
    public Result<String> updatePassword(
            @ApiParam(value = "管理员ID", required = true) @RequestParam Integer adminId,
            @ApiParam(value = "新密码", required = true) @RequestParam String newPassword) {
        adminService.updatePassword(adminId, newPassword);
        return Result.success("修改密码成功");
    }

    @PostMapping("/resetPassword")
    @ApiOperation("根据管理员ID重置密码（重置为默认密码 123456）")
    public Result<String> resetPassword(
            @ApiParam(value = "管理员ID", required = true) @RequestParam Integer adminId) {
        adminService.resetPassword(adminId);
        return Result.success("重置密码成功");
    }
}
