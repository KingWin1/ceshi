package com.community.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.community.backend.dto.ComplaintRecordQueryDTO;
import com.community.backend.service.ComplaintRecordService;
import com.community.common.pojo.ComplaintRecord;
import com.community.common.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 投诉记录管理接口
 */
@RestController
@RequestMapping("/api/complaintRecord")
@Api(tags = "投诉记录管理")
public class ComplaintRecordController {

    @Autowired
    private ComplaintRecordService complaintRecordService;

    @GetMapping("/page")
    @ApiOperation("分页+条件查询投诉记录")
    public Result<IPage<ComplaintRecord>> page(ComplaintRecordQueryDTO queryDTO) {
        IPage<ComplaintRecord> page = complaintRecordService.selectComplaintRecordPage(queryDTO);
        return Result.success(page);
    }

    @GetMapping("/detail")
    @ApiOperation("根据投诉记录ID查询指定投诉记录信息")
    public Result<ComplaintRecord> detail(
            @ApiParam(value = "投诉记录ID", required = true) @RequestParam Integer complaintId) {
        ComplaintRecord record = complaintRecordService.getById(complaintId);
        if (record == null) {
            return Result.error("40001", "投诉记录不存在");
        }
        return Result.success(record);
    }

    @PostMapping("/reply")
    @ApiOperation("根据投诉记录ID修改投诉记录状态=已回复")
    public Result<String> reply(
            @ApiParam(value = "投诉记录ID", required = true) @RequestParam Integer complaintId) {
        try {
            complaintRecordService.replyComplaint(complaintId);
            return Result.success("操作成功");
        } catch (RuntimeException e) {
            return Result.error("40001", e.getMessage());
        }
    }

    @PostMapping("/add")
    @ApiOperation("添加投诉记录")
    public Result<String> add(@RequestBody ComplaintRecord complaintRecord) {
        complaintRecordService.addComplaint(complaintRecord);
        return Result.success("添加成功");
    }

    @PostMapping("/batchDelete")
    @ApiOperation("根据投诉ID数组批量删除投诉记录")
    public Result<String> batchDelete(
            @ApiParam(value = "投诉ID数组", required = true) @RequestBody List<Integer> complaintIds) {
        complaintRecordService.removeByIds(complaintIds);
        return Result.success("批量删除成功");
    }

    @PostMapping("/updateStatus")
    @ApiOperation("根据投诉ID修改投诉状态")
    public Result<String> updateStatus(
            @ApiParam(value = "投诉ID", required = true) @RequestParam Integer complaintId,
            @ApiParam(value = "状态", required = true) @RequestParam Integer status) {
        try {
            complaintRecordService.updateComplaintStatus(complaintId, status);
            return Result.success("操作成功");
        } catch (RuntimeException e) {
            return Result.error("40001", e.getMessage());
        }
    }
}
