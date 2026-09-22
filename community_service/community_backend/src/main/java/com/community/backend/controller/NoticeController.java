package com.community.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.community.backend.dto.NoticeQueryDTO;
import com.community.backend.service.NoticeService;
import com.community.common.pojo.Notice;
import com.community.common.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公告管理接口
 */
@RestController
@RequestMapping("/api/notice")
@Api(tags = "公告管理")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @GetMapping("/page")
    @ApiOperation("分页+条件查询公告列表")
    public Result<IPage<Notice>> page(NoticeQueryDTO queryDTO) {
        IPage<Notice> page = noticeService.selectNoticePage(queryDTO);
        return Result.success(page);
    }

    @PostMapping("/add")
    @ApiOperation("添加公告")
    public Result<String> add(@RequestBody Notice notice) {
        noticeService.save(notice);
        return Result.success("添加成功");
    }

    @GetMapping("/getById")
    @ApiOperation("根据ID查询公告信息")
    public Result<Notice> getById(
            @ApiParam(value = "公告ID", required = true) @RequestParam Integer noticeId) {
        Notice notice = noticeService.getNoticeById(noticeId);
        if (notice == null) {
            return Result.error("40001", "公告信息不存在");
        }
        return Result.success(notice);
    }

    @PostMapping("/update")
    @ApiOperation("根据ID修改公告信息")
    public Result<String> update(@RequestBody Notice notice) {
        noticeService.updateById(notice);
        return Result.success("修改成功");
    }

    @DeleteMapping("/delete")
    @ApiOperation("根据ID删除公告")
    public Result<String> delete(
            @ApiParam(value = "公告ID", required = true) @RequestParam Integer noticeId) {
        noticeService.removeById(noticeId);
        return Result.success("删除成功");
    }

    @PostMapping("/batchDelete")
    @ApiOperation("批量删除公告")
    public Result<String> batchDelete(
            @ApiParam(value = "公告ID数组", required = true) @RequestBody List<Integer> noticeIds) {
        noticeService.removeByIds(noticeIds);
        return Result.success("批量删除成功");
    }

    @PostMapping("/updateStatus")
    @ApiOperation("根据ID修改公告状态")
    public Result<String> updateStatus(
            @ApiParam(value = "公告ID", required = true) @RequestParam Integer noticeId,
            @ApiParam(value = "状态", required = true) @RequestParam Integer status) {
        Notice notice = new Notice();
        notice.setNoticeId(noticeId);
        notice.setStatus(status);
        noticeService.updateById(notice);
        return Result.success("状态修改成功");
    }

    @PostMapping("/batchUpdateStatus")
    @ApiOperation("批量修改公告状态")
    public Result<String> batchUpdateStatus(
            @ApiParam(value = "公告ID数组", required = true) @RequestBody List<Integer> noticeIds,
            @ApiParam(value = "状态", required = true) @RequestParam Integer status) {
        for (Integer noticeId : noticeIds) {
            Notice notice = new Notice();
            notice.setNoticeId(noticeId);
            notice.setStatus(status);
            noticeService.updateById(notice);
        }
        return Result.success("批量修改状态成功");
    }

    @PostMapping("/batchUpdateTop")
    @ApiOperation("批量修改公告置顶状态")
    public Result<String> batchUpdateTop(
            @ApiParam(value = "公告ID数组", required = true) @RequestBody List<Integer> noticeIds,
            @ApiParam(value = "是否置顶：1-是 2-否", required = true) @RequestParam Integer isTop) {
        for (Integer noticeId : noticeIds) {
            Notice notice = new Notice();
            notice.setNoticeId(noticeId);
            notice.setIsTop(isTop);
            noticeService.updateById(notice);
        }
        return Result.success("批量修改置顶状态成功");
    }
}
