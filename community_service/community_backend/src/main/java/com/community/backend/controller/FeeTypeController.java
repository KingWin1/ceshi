package com.community.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.community.backend.dto.FeeTypeQueryDTO;
import com.community.backend.service.FeeTypeService;
import com.community.common.pojo.FeeType;
import com.community.common.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 费用类型管理接口
 */
@RestController
@RequestMapping("/api/feeType")
@Api(tags = "费用类型管理")
public class FeeTypeController {

    @Autowired
    private FeeTypeService feeTypeService;

    @GetMapping("/page")
    @ApiOperation("分页+条件查询费用类型列表")
    public Result<IPage<FeeType>> page(FeeTypeQueryDTO queryDTO) {
        IPage<FeeType> page = feeTypeService.selectFeeTypePage(queryDTO);
        return Result.success(page);
    }

    @GetMapping("/listAll")
    @ApiOperation("查询全部费用类型")
    public Result<List<FeeType>> listAll() {
        List<FeeType> list = feeTypeService.listAll();
        return Result.success(list);
    }

    @PostMapping("/listByIds")
    @ApiOperation("根据费用类型ID数组查询对应的费用类型信息")
    public Result<List<FeeType>> listByIds(
            @ApiParam(value = "费用类型ID数组", required = true) @RequestBody List<Integer> feeTypeIds) {
        List<FeeType> list = feeTypeService.listByIds(feeTypeIds);
        return Result.success(list);
    }

    @PostMapping("/add")
    @ApiOperation("添加费用类型")
    public Result<String> add(@RequestBody FeeType feeType) {
        feeTypeService.addFeeType(feeType);
        return Result.success("添加成功");
    }

    @GetMapping("/getById")
    @ApiOperation("根据费用类型ID查询指定费用类型信息")
    public Result<FeeType> getById(
            @ApiParam(value = "费用类型ID", required = true) @RequestParam Integer feeTypeId) {
        FeeType feeType = feeTypeService.getById(feeTypeId);
        if (feeType == null) {
            return Result.error("40001", "费用类型不存在");
        }
        return Result.success(feeType);
    }

    @PostMapping("/update")
    @ApiOperation("根据费用类型ID修改指定费用类型信息")
    public Result<String> update(@RequestBody FeeType feeType) {
        feeTypeService.updateFeeType(feeType);
        return Result.success("修改成功");
    }

    @DeleteMapping("/delete")
    @ApiOperation("根据费用类型ID删除指定费用类型信息")
    public Result<String> delete(
            @ApiParam(value = "费用类型ID", required = true) @RequestParam Integer feeTypeId) {
        try {
            feeTypeService.deleteFeeType(feeTypeId);
            return Result.success("删除成功");
        } catch (RuntimeException e) {
            return Result.error("40001", e.getMessage());
        }
    }

    @PostMapping("/updatePrice")
    @ApiOperation("根据费用类型ID修改单价信息")
    public Result<String> updatePrice(
            @ApiParam(value = "费用类型ID", required = true) @RequestParam Integer feeTypeId,
            @ApiParam(value = "按月单价") @RequestParam(required = false) BigDecimal monthlyPrice,
            @ApiParam(value = "按季度单价") @RequestParam(required = false) BigDecimal quarterlyPrice,
            @ApiParam(value = "按年单价") @RequestParam(required = false) BigDecimal yearlyPrice) {
        feeTypeService.updatePrice(feeTypeId, monthlyPrice, quarterlyPrice, yearlyPrice);
        return Result.success("单价修改成功");
    }
}
