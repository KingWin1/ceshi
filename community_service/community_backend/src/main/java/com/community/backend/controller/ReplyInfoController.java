package com.community.backend.controller;

import com.community.backend.service.ReplyInfoService;
import com.community.common.pojo.ReplyInfo;
import com.community.common.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 回复信息管理接口
 */
@RestController
@RequestMapping("/api/replyInfo")
@Api(tags = "回复信息管理")
public class ReplyInfoController {

    @Autowired
    private ReplyInfoService replyInfoService;

    @GetMapping("/list")
    @ApiOperation("根据投诉记录ID、回复人类型、回复人ID查询回复信息集合")
    public Result<List<ReplyInfo>> list(
            @ApiParam(value = "投诉记录ID", required = true) @RequestParam Integer complaintId,
            @ApiParam(value = "回复人类型：1-用户 2-管理员", required = true) @RequestParam Integer replyType,
            @ApiParam(value = "回复人ID", required = true) @RequestParam Integer replyerId) {
        List<ReplyInfo> list = replyInfoService.getReplyList(complaintId, replyType, replyerId);
        return Result.success(list);
    }

    @PostMapping("/add")
    @ApiOperation("添加回复信息")
    public Result<String> add(@RequestBody ReplyInfo replyInfo) {
        replyInfoService.addReply(replyInfo);
        return Result.success("添加成功");
    }

    @PostMapping("/delete")
    @ApiOperation("根据回复ID删除回复信息")
    public Result<String> delete(
            @ApiParam(value = "回复ID", required = true) @RequestParam Integer id) {
        replyInfoService.removeById(id);
        return Result.success("删除成功");
    }
}
