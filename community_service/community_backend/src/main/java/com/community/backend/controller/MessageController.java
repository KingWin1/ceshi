package com.community.backend.controller;

import com.community.backend.dto.MessageAddDTO;
import com.community.backend.service.MessageService;
import com.community.common.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 消息管理接口
 */
@RestController
@RequestMapping("/api/message")
@Api(tags = "消息管理")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/add")
    @ApiOperation("添加消息（催缴）")
    public Result<String> add(
            @ApiParam(value = "账单ID", required = true) @RequestParam Integer billId,
            @ApiParam(value = "居民ID", required = true) @RequestParam Integer residentId,
            @ApiParam(value = "消息标题", required = true) @RequestParam String title,
            @ApiParam(value = "消息内容", required = true) @RequestParam String content) {
        messageService.addMessage(billId, residentId, title, content);
        return Result.success("添加成功");
    }

    @PostMapping("/batchAdd")
    @ApiOperation("批量添加消息（批量催缴）")
    public Result<String> batchAdd(
            @ApiParam(value = "消息对象集合", required = true) @RequestBody List<MessageAddDTO> addDTOList) {
        messageService.batchAddMessage(addDTOList);
        return Result.success("批量催缴成功");
    }
}
