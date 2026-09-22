package com.community.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.community.backend.dto.CustomerServiceQueryDTO;
import com.community.backend.service.CustomerServiceService;
import com.community.common.pojo.CustomerService;
import com.community.common.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 客服管理接口
 */
@RestController
@RequestMapping("/api/customerService")
@Api(tags = "客服管理")
public class CustomerServiceController {

    @Autowired
    private CustomerServiceService customerServiceService;

    @GetMapping("/page")
    @ApiOperation("分页+条件查询客服列表")
    public Result<IPage<CustomerService>> page(CustomerServiceQueryDTO queryDTO) {
        IPage<CustomerService> page = customerServiceService.selectCustomerServicePage(queryDTO);
        return Result.success(page);
    }

    @PostMapping("/add")
    @ApiOperation("添加客服信息")
    public Result<String> add(@RequestBody CustomerService customerService) {
        customerServiceService.save(customerService);
        return Result.success("添加成功");
    }

    @GetMapping("/getById")
    @ApiOperation("根据ID查询客服信息")
    public Result<CustomerService> getById(
            @ApiParam(value = "客服ID", required = true) @RequestParam Integer csId) {
        CustomerService customerService = customerServiceService.getCustomerServiceById(csId);
        if (customerService == null) {
            return Result.error("40001", "客服信息不存在");
        }
        return Result.success(customerService);
    }

    @PostMapping("/update")
    @ApiOperation("修改客服信息")
    public Result<String> update(@RequestBody CustomerService customerService) {
        customerServiceService.updateById(customerService);
        return Result.success("修改成功");
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除客服信息")
    public Result<String> delete(
            @ApiParam(value = "客服ID", required = true) @RequestParam Integer csId) {
        customerServiceService.removeById(csId);
        return Result.success("删除成功");
    }

    @PostMapping("/batchDelete")
    @ApiOperation("批量删除客服信息")
    public Result<String> batchDelete(
            @ApiParam(value = "客服ID数组", required = true) @RequestBody List<Integer> csIds) {
        customerServiceService.removeByIds(csIds);
        return Result.success("批量删除成功");
    }
}
