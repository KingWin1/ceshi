package com.community.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.community.backend.dto.HouseResidentAuthAddDTO;
import com.community.common.dto.HouseResidentAuthQueryDTO;
import com.community.backend.dto.HouseResidentAuthUpdateDTO;
import com.community.backend.service.HouseResidentAuthService;
import com.community.common.vo.HouseAuthCountVO;
import com.community.common.vo.HouseResidentAuthVO;
import com.community.common.vo.ResidentAuthCountVO;
import com.community.common.pojo.HouseResidentAuth;
import com.community.common.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 房屋居民认证管理接口
 */
@RestController
@RequestMapping("/api/auth")
@Api(tags = "认证管理")
public class HouseResidentAuthController {

    @Autowired
    private HouseResidentAuthService houseResidentAuthService;

    @GetMapping("/page")
    @ApiOperation("分页+条件查询认证列表")
    public Result<IPage<HouseResidentAuthVO>> page(HouseResidentAuthQueryDTO queryDTO) {
        IPage<HouseResidentAuthVO> page = houseResidentAuthService.selectAuthPage(queryDTO);
        return Result.success(page);
    }

    @DeleteMapping("/delete")
    @ApiOperation("根据认证ID删除认证信息")
    public Result<String> delete(
            @ApiParam(value = "认证ID", required = true) @RequestParam Integer authId) {
        houseResidentAuthService.removeById(authId);
        return Result.success("删除成功");
    }

    @PostMapping("/batchDelete")
    @ApiOperation("根据认证ID数组批量删除认证信息")
    public Result<String> batchDelete(
            @ApiParam(value = "认证ID数组", required = true) @RequestBody List<Integer> authIds) {
        houseResidentAuthService.removeByIds(authIds);
        return Result.success("批量删除成功");
    }

    @PostMapping("/updateAuditStatus")
    @ApiOperation("根据认证ID修改审核状态")
    public Result<String> updateAuditStatus(
            @ApiParam(value = "认证ID", required = true) @RequestParam Integer authId,
            @ApiParam(value = "审核状态", required = true) @RequestParam Integer auditStatus,
            @ApiParam(value = "审核备注") @RequestParam(required = false) String auditRemark,
            @ApiParam(value = "管理员ID", required = true) @RequestParam Integer adminId) {
        houseResidentAuthService.updateAuditStatus(authId, auditStatus, auditRemark, adminId);
        return Result.success("审核状态修改成功");
    }

    @PostMapping("/batchUpdateAuditStatus")
    @ApiOperation("根据认证ID数组批量修改审核状态")
    public Result<String> batchUpdateAuditStatus(
            @ApiParam(value = "认证ID数组", required = true) @RequestBody List<Integer> authIds,
            @ApiParam(value = "审核状态", required = true) @RequestParam Integer auditStatus,
            @ApiParam(value = "审核备注") @RequestParam(required = false) String auditRemark,
            @ApiParam(value = "管理员ID", required = true) @RequestParam Integer adminId) {
        houseResidentAuthService.batchUpdateAuditStatus(authIds, auditStatus, auditRemark, adminId);
        return Result.success("批量审核状态修改成功");
    }

    @PostMapping("/add")
    @ApiOperation("添加认证信息")
    public Result<String> add(@RequestBody HouseResidentAuthAddDTO addDTO) {
        houseResidentAuthService.addAuth(addDTO);
        return Result.success("添加成功");
    }

    @GetMapping("/getById")
    @ApiOperation("根据认证ID查询指定认证信息")
    public Result<HouseResidentAuth> getById(
            @ApiParam(value = "认证ID", required = true) @RequestParam Integer authId) {
        HouseResidentAuth auth = houseResidentAuthService.getAuthById(authId);
        if (auth == null) {
            return Result.error("40001", "认证信息不存在");
        }
        return Result.success(auth);
    }

    @PostMapping("/update")
    @ApiOperation("根据认证ID修改指定认证信息")
    public Result<String> update(@RequestBody HouseResidentAuthUpdateDTO updateDTO) {
        houseResidentAuthService.updateAuth(updateDTO);
        return Result.success("修改成功");
    }

    @GetMapping("/countByHouseId")
    @ApiOperation("根据房屋ID统计该房屋在认证表中出现的次数")
    public Result<Integer> countByHouseId(
            @ApiParam(value = "房屋ID", required = true) @RequestParam Integer houseId) {
        return Result.success(houseResidentAuthService.countByHouseId(houseId));
    }

    @PostMapping("/countByHouseIds")
    @ApiOperation("根据房屋ID数组统计每个房屋在认证表中出现的次数")
    public Result<List<HouseAuthCountVO>> countByHouseIds(
            @ApiParam(value = "房屋ID数组", required = true) @RequestBody List<Integer> houseIds) {
        return Result.success(houseResidentAuthService.countAuthByHouseIds(houseIds));
    }

    @PostMapping("/listByHouseIds")
    @ApiOperation("根据房屋ID数组批量查询认证表数据")
    public Result<List<HouseResidentAuth>> listByHouseIds(
            @ApiParam(value = "房屋ID数组", required = true) @RequestBody List<Integer> houseIds) {
        return Result.success(houseResidentAuthService.listByHouseIds(houseIds));
    }

    @GetMapping("/countByResidentId")
    @ApiOperation("根据居民ID统计该居民在认证表中出现的次数")
    public Result<Integer> countByResidentId(
            @ApiParam(value = "居民ID", required = true) @RequestParam Integer residentId) {
        return Result.success(houseResidentAuthService.countByResidentId(residentId));
    }

    @PostMapping("/countByResidentIds")
    @ApiOperation("根据居民ID数组统计每个居民在认证表中出现的次数")
    public Result<List<ResidentAuthCountVO>> countByResidentIds(
            @ApiParam(value = "居民ID数组", required = true) @RequestBody List<Integer> residentIds) {
        return Result.success(houseResidentAuthService.countAuthByResidentIds(residentIds));
    }

    @PostMapping("/listByResidentIds")
    @ApiOperation("根据居民ID数组批量查询认证表数据")
    public Result<List<HouseResidentAuth>> listByResidentIds(
            @ApiParam(value = "居民ID数组", required = true) @RequestBody List<Integer> residentIds) {
        return Result.success(houseResidentAuthService.listByResidentIds(residentIds));
    }
}
