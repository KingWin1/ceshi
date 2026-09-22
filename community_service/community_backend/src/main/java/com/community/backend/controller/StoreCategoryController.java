package com.community.backend.controller;

import com.community.backend.service.StoreCategoryService;
import com.community.common.pojo.StoreCategory;
import com.community.common.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 门店分类管理接口
 */
@RestController
@RequestMapping("/api/storeCategory")
@Api(tags = "门店分类管理")
public class StoreCategoryController {

    @Autowired
    private StoreCategoryService storeCategoryService;

    @GetMapping("/listAll")
    @ApiOperation("查询全部的门店分类信息")
    public Result<List<StoreCategory>> listAll() {
        List<StoreCategory> list = storeCategoryService.listAll();
        return Result.success(list);
    }
}
