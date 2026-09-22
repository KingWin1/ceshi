package com.community.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.community.common.dto.ResidentAddDTO;
import com.community.common.dto.ResidentQueryDTO;
import com.community.common.dto.ResidentUpdateDTO;
import com.community.common.service.ResidentService;
import com.community.common.vo.ResidentVO;
import com.community.common.pojo.Resident;
import com.community.common.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 居民管理接口
 */
@RestController
@RequestMapping("/api/resident")
@Api(tags = "居民管理")
public class ResidentController {

    @Autowired
    private ResidentService residentService;

    @GetMapping("/page")
    @ApiOperation("分页+条件查询居民列表")
    public Result<IPage<ResidentVO>> page(ResidentQueryDTO queryDTO) {
        IPage<ResidentVO> page = residentService.selectResidentPage(queryDTO);
        return Result.success(page);
    }

    @DeleteMapping("/delete")
    @ApiOperation("根据ID删除居民信息")
    public Result<String> delete(
            @ApiParam(value = "居民ID", required = true) @RequestParam Integer residentId) {
        residentService.deleteResident(residentId);
        return Result.success("删除成功");
    }

    @PostMapping("/batchDelete")
    @ApiOperation("批量删除居民信息")
    public Result<String> batchDelete(
            @ApiParam(value = "居民ID数组", required = true) @RequestBody List<Integer> residentIds) {
        residentService.deleteResidentBatch(residentIds);
        return Result.success("批量删除成功");
    }

    @PostMapping("/add")
    @ApiOperation("添加居民")
    public Result<String> add(@RequestBody ResidentAddDTO addDTO) {
        residentService.addResident(addDTO);
        return Result.success("添加成功");
    }

    @GetMapping("/getForUpdate")
    @ApiOperation("修改回显：查询居民信息和该居民的房屋信息集合")
    public Result<Map<String, Object>> getForUpdate(
            @ApiParam(value = "居民ID", required = true) @RequestParam Integer residentId) {
        Map<String, Object> result = residentService.getResidentForUpdate(residentId);
        return Result.success(result);
    }

    @PostMapping("/update")
    @ApiOperation("修改居民信息")
    public Result<String> update(@RequestBody ResidentUpdateDTO updateDTO) {
        residentService.updateResident(updateDTO);
        return Result.success("修改成功");
    }

    @GetMapping("/search")
    @ApiOperation("根据姓名或电话查询指定居民信息")
    public Result<List<Resident>> search(
            @ApiParam(value = "姓名") @RequestParam(required = false) String name,
            @ApiParam(value = "电话") @RequestParam(required = false) String phone) {
        List<Resident> list = residentService.searchByNameOrPhone(name, phone);
        return Result.success(list);
    }
}
