package com.community.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.community.backend.dto.AdvertisementQueryDTO;
import com.community.backend.service.AdvertisementService;
import com.community.common.pojo.Advertisement;
import com.community.common.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 广告管理接口
 */
@RestController
@RequestMapping("/api/advertisement")
@Api(tags = "广告管理")
public class AdvertisementController {

    @Autowired
    private AdvertisementService advertisementService;

    @GetMapping("/page")
    @ApiOperation("分页+条件查询广告列表")
    public Result<IPage<Advertisement>> page(AdvertisementQueryDTO queryDTO) {
        IPage<Advertisement> page = advertisementService.selectAdvertisementPage(queryDTO);
        return Result.success(page);
    }

    @PostMapping("/add")
    @ApiOperation("添加广告")
    public Result<String> add(@RequestBody Advertisement advertisement) {
        advertisementService.save(advertisement);
        return Result.success("添加成功");
    }

    @GetMapping("/getById")
    @ApiOperation("根据ID查询广告信息")
    public Result<Advertisement> getById(
            @ApiParam(value = "广告ID", required = true) @RequestParam Integer adId) {
        Advertisement advertisement = advertisementService.getById(adId);
        if (advertisement == null) {
            return Result.error("40001", "广告信息不存在");
        }
        return Result.success(advertisement);
    }

    @PostMapping("/update")
    @ApiOperation("根据ID修改广告信息")
    public Result<String> update(@RequestBody Advertisement advertisement) {
        advertisementService.updateById(advertisement);
        return Result.success("修改成功");
    }

    @DeleteMapping("/delete")
    @ApiOperation("根据ID删除广告")
    public Result<String> delete(
            @ApiParam(value = "广告ID", required = true) @RequestParam Integer adId) {
        advertisementService.removeById(adId);
        return Result.success("删除成功");
    }

    @PostMapping("/batchDelete")
    @ApiOperation("批量删除广告")
    public Result<String> batchDelete(
            @ApiParam(value = "广告ID数组", required = true) @RequestBody List<Integer> adIds) {
        advertisementService.removeByIds(adIds);
        return Result.success("批量删除成功");
    }

    @PostMapping("/updateStatus")
    @ApiOperation("根据ID修改广告状态")
    public Result<String> updateStatus(
            @ApiParam(value = "广告ID", required = true) @RequestParam Integer adId,
            @ApiParam(value = "状态：1-启用 2-停用", required = true) @RequestParam Integer status) {
        advertisementService.updateStatus(adId, status);
        return Result.success("状态修改成功");
    }
}
