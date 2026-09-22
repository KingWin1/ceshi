package com.community.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.community.backend.dto.PositionQueryDTO;
import com.community.backend.service.PositionService;
import com.community.backend.vo.PositionEmployeeCountVO;
import com.community.common.pojo.Position;
import com.community.common.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 岗位管理接口
 */
@RestController
@RequestMapping("/api/position")
@Api(tags = "岗位管理")
public class PositionController {

    @Autowired
    private PositionService positionService;

    @GetMapping("/page")
    @ApiOperation("分页+条件查询岗位列表")
    public Result<IPage<Position>> page(PositionQueryDTO queryDTO) {
        IPage<Position> page = positionService.selectPositionPage(queryDTO);
        return Result.success(page);
    }

    @PostMapping("/add")
    @ApiOperation("添加岗位")
    public Result<String> add(@RequestBody Position position) {
        positionService.save(position);
        return Result.success("添加成功");
    }

    @GetMapping("/getById")
    @ApiOperation("根据ID查询岗位信息")
    public Result<Position> getById(
            @ApiParam(value = "岗位ID", required = true) @RequestParam Integer positionId) {
        Position position = positionService.getPositionById(positionId);
        if (position == null) {
            return Result.error("40001", "岗位信息不存在");
        }
        return Result.success(position);
    }

    @PostMapping("/update")
    @ApiOperation("修改岗位信息")
    public Result<String> update(@RequestBody Position position) {
        positionService.updateById(position);
        return Result.success("修改成功");
    }

    @DeleteMapping("/delete")
    @ApiOperation("根据ID删除岗位（联动删除该岗位下的员工数据）")
    public Result<String> delete(
            @ApiParam(value = "岗位ID", required = true) @RequestParam Integer positionId) {
        positionService.deletePosition(positionId);
        return Result.success("删除成功");
    }

    @PostMapping("/batchDelete")
    @ApiOperation("批量删除岗位（联动删除这些岗位下的员工数据）")
    public Result<String> batchDelete(
            @ApiParam(value = "岗位ID数组", required = true) @RequestBody List<Integer> positionIds) {
        positionService.deletePositionBatch(positionIds);
        return Result.success("批量删除成功");
    }

    @GetMapping("/countEmployeeByPositionId")
    @ApiOperation("根据岗位ID统计该岗位在员工表中出现的次数")
    public Result<Integer> countEmployeeByPositionId(
            @ApiParam(value = "岗位ID", required = true) @RequestParam Integer positionId) {
        int count = positionService.countEmployeeByPositionId(positionId);
        return Result.success(count);
    }

    @PostMapping("/countEmployeeByPositionIds")
    @ApiOperation("根据岗位ID数组统计每个岗位在员工表中出现的次数")
    public Result<List<PositionEmployeeCountVO>> countEmployeeByPositionIds(
            @ApiParam(value = "岗位ID数组", required = true) @RequestBody List<Integer> positionIds) {
        List<PositionEmployeeCountVO> list = positionService.countEmployeeByPositionIds(positionIds);
        return Result.success(list);
    }

    @GetMapping("/listByDeptId")
    @ApiOperation("根据部门ID查询岗位列表")
    public Result<List<Position>> listByDeptId(
            @ApiParam(value = "部门ID", required = true) @RequestParam Integer deptId) {
        List<Position> list = positionService.listByDeptId(deptId);
        return Result.success(list);
    }

    @GetMapping("/listAll")
    @ApiOperation("查询全部岗位信息")
    public Result<List<Position>> listAll() {
        List<Position> list = positionService.listAll();
        return Result.success(list);
    }
}
