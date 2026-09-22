package com.community.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.community.backend.dto.BuildingQueryDTO;
import com.community.backend.service.BuildingService;
import com.community.common.pojo.Building;
import com.community.common.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 楼栋管理接口
 */
@RestController
@RequestMapping("/api/building")
@Api(tags = "楼栋管理")
public class BuildingController {

    @Autowired
    private BuildingService buildingService;

    @GetMapping("/page")
    @ApiOperation("分页+条件查询楼栋列表")
    public Result<IPage<Building>> page(BuildingQueryDTO queryDTO) {
        IPage<Building> page = buildingService.selectBuildingPage(queryDTO);
        return Result.success(page);
    }

    @PostMapping("/add")
    @ApiOperation("添加楼栋信息")
    public Result<String> add(@RequestBody Building building) {
        buildingService.save(building);
        return Result.success("添加成功");
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除楼栋信息")
    public Result<String> delete(
            @ApiParam(value = "楼栋ID", required = true) @RequestParam Integer buildingId) {
        buildingService.deleteBuilding(buildingId);
        return Result.success("删除成功");
    }

    @PostMapping("/batchDelete")
    @ApiOperation("批量删除楼栋信息")
    public Result<Map<String, Object>> batchDelete(
            @ApiParam(value = "楼栋ID数组", required = true) @RequestBody List<Integer> buildingIds) {
        return Result.success(buildingService.deleteBuildingBatch(buildingIds));
    }

    @GetMapping("/getById")
    @ApiOperation("根据ID查询楼栋信息")
    public Result<Building> getById(
            @ApiParam(value = "楼栋ID", required = true) @RequestParam Integer buildingId) {
        Building building = buildingService.getBuildingById(buildingId);
        if (building == null) {
            return Result.error("40001", "楼栋信息不存在");
        }
        return Result.success(building);
    }

    @PostMapping("/update")
    @ApiOperation("修改楼栋信息")
    public Result<String> update(@RequestBody Building building) {
        buildingService.updateById(building);
        return Result.success("修改成功");
    }

    @GetMapping("/list")
    @ApiOperation("查询全部楼栋信息")
    public Result<List<Building>> list() {
        List<Building> list = buildingService.list();
        return Result.success(list);
    }
}
