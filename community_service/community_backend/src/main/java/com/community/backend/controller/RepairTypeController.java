package com.community.backend.controller;

import com.community.backend.service.RepairTypeService;
import com.community.common.pojo.RepairType;
import com.community.common.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 报修类型管理接口
 */
@RestController
@RequestMapping("/api/repairType")
@Api(tags = "报修类型管理")
public class RepairTypeController {

    @Autowired
    private RepairTypeService repairTypeService;

    @GetMapping("/listAll")
    @ApiOperation("查询全部报修类型信息")
    public Result<List<RepairType>> listAll() {
        List<RepairType> list = repairTypeService.listAll();
        return Result.success(list);
    }
}
