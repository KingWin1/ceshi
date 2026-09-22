package com.community.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.community.backend.dto.RepairRecordQueryDTO;
import com.community.backend.service.RepairRecordService;
import com.community.backend.vo.RepairRecordVO;
import com.community.common.pojo.RepairRecord;
import com.community.common.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 报修记录管理接口
 */
@RestController
@RequestMapping("/api/repairRecord")
@Api(tags = "报修记录管理")
public class RepairRecordController {

    @Autowired
    private RepairRecordService repairRecordService;

    @GetMapping("/page")
    @ApiOperation("分页+条件查询报修记录")
    public Result<IPage<RepairRecordVO>> page(RepairRecordQueryDTO queryDTO) {
        IPage<RepairRecordVO> page = repairRecordService.selectRepairRecordPage(queryDTO);
        return Result.success(page);
    }

    @GetMapping("/detail")
    @ApiOperation("根据报修ID查询报修记录详情")
    public Result<RepairRecordVO> detail(
            @ApiParam(value = "报修ID", required = true) @RequestParam Integer repairId) {
        RepairRecordVO vo = repairRecordService.getRepairRecordDetail(repairId);
        if (vo == null) {
            return Result.error("40001", "报修记录不存在");
        }
        return Result.success(vo);
    }

    @PostMapping("/dispatch")
    @ApiOperation("派单")
    public Result<String> dispatch(
            @ApiParam(value = "报修ID", required = true) @RequestParam Integer repairId,
            @ApiParam(value = "员工ID", required = true) @RequestParam Integer employeeId) {
        try {
            repairRecordService.dispatch(repairId, employeeId);
            return Result.success("派单成功");
        } catch (RuntimeException e) {
            return Result.error("40001", e.getMessage());
        }
    }

    @PostMapping("/complete")
    @ApiOperation("完工")
    public Result<String> complete(
            @ApiParam(value = "报修ID", required = true) @RequestParam Integer repairId,
            @ApiParam(value = "完工备注") @RequestParam(required = false) String completeRemark,
            @ApiParam(value = "完工图片地址") @RequestParam(required = false) String completeImageUrl) {
        try {
            repairRecordService.complete(repairId, completeRemark, completeImageUrl);
            return Result.success("完工成功");
        } catch (RuntimeException e) {
            return Result.error("40001", e.getMessage());
        }
    }

    @PostMapping("/add")
    @ApiOperation("添加报修记录")
    public Result<String> add(@RequestBody RepairRecord repairRecord) {
        repairRecordService.addRepairRecord(repairRecord);
        return Result.success("添加成功");
    }

    @PostMapping("/batchDelete")
    @ApiOperation("根据报修ID数组批量删除报修记录")
    public Result<String> batchDelete(
            @ApiParam(value = "报修ID数组", required = true) @RequestBody List<Integer> repairIds) {
        repairRecordService.removeByIds(repairIds);
        return Result.success("批量删除成功");
    }
}
