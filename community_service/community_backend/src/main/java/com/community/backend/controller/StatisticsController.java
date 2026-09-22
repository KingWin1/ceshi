package com.community.backend.controller;

import com.community.backend.service.StatisticsService;
import com.community.backend.vo.FeeChartVO;
import com.community.backend.vo.HomeBillVO;
import com.community.backend.vo.StatisticsVO;
import com.community.backend.vo.WorkOrderChartVO;
import com.community.common.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页统计接口
 */
@RestController
@RequestMapping("/api/statistics")
@Api(tags = "首页统计")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/home")
    @ApiOperation("首页聚合数据（统计卡片+图表+账单列表）")
    public Result<Map<String, Object>> home(
            @ApiParam(value = "列表条数，默认5") @RequestParam(defaultValue = "5") Integer limit) {
        Map<String, Object> data = new HashMap<>();
        data.put("statistics", statisticsService.getStatistics());
        data.put("feeChart", statisticsService.getFeeChart());
        data.put("workOrderChart", statisticsService.getWorkOrderChart());
        data.put("latestRepairs", statisticsService.getLatestRepairs(limit));
        data.put("latestComplaints", statisticsService.getLatestComplaints(limit));
        data.put("unpaidBills", statisticsService.getUnpaidBills(limit));
        data.put("recentPaidBills", statisticsService.getRecentPaidBills(limit));
        return Result.success(data);
    }

    @GetMapping("/statistics")
    @ApiOperation("首页统计卡片数据")
    public Result<StatisticsVO> statistics() {
        return Result.success(statisticsService.getStatistics());
    }

    @GetMapping("/feeChart")
    @ApiOperation("近6个月收费统计")
    public Result<FeeChartVO> feeChart() {
        return Result.success(statisticsService.getFeeChart());
    }

    @GetMapping("/workOrderChart")
    @ApiOperation("服务工单统计")
    public Result<WorkOrderChartVO> workOrderChart() {
        return Result.success(statisticsService.getWorkOrderChart());
    }

    @GetMapping("/unpaidBills")
    @ApiOperation("待缴账单列表")
    public Result<List<HomeBillVO>> unpaidBills(
            @ApiParam(value = "列表条数，默认5") @RequestParam(defaultValue = "5") Integer limit) {
        return Result.success(statisticsService.getUnpaidBills(limit));
    }

    @GetMapping("/recentPaidBills")
    @ApiOperation("最近缴费列表")
    public Result<List<HomeBillVO>> recentPaidBills(
            @ApiParam(value = "列表条数，默认5") @RequestParam(defaultValue = "5") Integer limit) {
        return Result.success(statisticsService.getRecentPaidBills(limit));
    }
}
