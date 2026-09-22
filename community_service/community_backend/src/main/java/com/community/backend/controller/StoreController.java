package com.community.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.community.backend.dto.StoreQueryDTO;
import com.community.backend.service.StoreService;
import com.community.backend.vo.StoreVO;
import com.community.common.pojo.Store;
import com.community.common.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 门店管理接口
 */
@RestController
@RequestMapping("/api/store")
@Api(tags = "门店管理")
public class StoreController {

    @Autowired
    private StoreService storeService;

    @GetMapping("/page")
    @ApiOperation("分页+条件查询门店列表")
    public Result<IPage<Store>> page(StoreQueryDTO queryDTO) {
        IPage<Store> page = storeService.selectStorePage(queryDTO);
        return Result.success(page);
    }

    @PostMapping("/add")
    @ApiOperation("添加门店信息")
    public Result<String> add(@RequestBody Store store) {
        storeService.save(store);
        return Result.success("添加成功");
    }

    @GetMapping("/getById")
    @ApiOperation("根据ID查询门店信息")
    public Result<Store> getById(
            @ApiParam(value = "门店ID", required = true) @RequestParam Integer storeId) {
        Store store = storeService.getStoreById(storeId);
        if (store == null) {
            return Result.error("40001", "门店信息不存在");
        }
        return Result.success(store);
    }

    @PostMapping("/update")
    @ApiOperation("修改门店信息")
    public Result<String> update(@RequestBody Store store) {
        storeService.updateById(store);
        return Result.success("修改成功");
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除门店信息")
    public Result<String> delete(
            @ApiParam(value = "门店ID", required = true) @RequestParam Integer storeId) {
        storeService.removeById(storeId);
        return Result.success("删除成功");
    }

    @PostMapping("/batchDelete")
    @ApiOperation("批量删除门店信息")
    public Result<String> batchDelete(
            @ApiParam(value = "门店ID数组", required = true) @RequestBody List<Integer> storeIds) {
        storeService.removeByIds(storeIds);
        return Result.success("批量删除成功");
    }
}
