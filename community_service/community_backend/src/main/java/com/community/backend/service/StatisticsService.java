package com.community.backend.service;

import com.community.backend.vo.FeeChartVO;
import com.community.backend.vo.HomeBillVO;
import com.community.backend.vo.StatisticsVO;
import com.community.backend.vo.WorkOrderChartVO;
import com.community.common.pojo.ComplaintRecord;
import com.community.common.pojo.RepairRecord;

import java.util.List;

/**
 * 首页统计Service接口
 */
public interface StatisticsService {

    /**
     * 首页统计卡片数据
     */
    StatisticsVO getStatistics();

    /**
     * 近6个月收费统计
     */
    FeeChartVO getFeeChart();

    /**
     * 服务工单统计
     */
    WorkOrderChartVO getWorkOrderChart();

    /**
     * 待缴账单列表
     */
    List<HomeBillVO> getUnpaidBills(Integer limit);

    /**
     * 最近缴费列表
     */
    List<HomeBillVO> getRecentPaidBills(Integer limit);

    /**
     * 最新报修动态列表
     */
    List<RepairRecord> getLatestRepairs(Integer limit);

    /**
     * 最新投诉动态列表
     */
    List<ComplaintRecord> getLatestComplaints(Integer limit);
}
