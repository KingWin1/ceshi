package com.community.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.community.backend.mapper.ComplaintRecordMapper;
import com.community.backend.mapper.RepairRecordMapper;
import com.community.backend.mapper.StatisticsMapper;
import com.community.backend.service.StatisticsService;
import com.community.backend.vo.FeeChartVO;
import com.community.backend.vo.HomeBillVO;
import com.community.backend.vo.StatisticsVO;
import com.community.backend.vo.WorkOrderChartVO;
import com.community.common.pojo.ComplaintRecord;
import com.community.common.pojo.RepairRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 首页统计Service实现类
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private StatisticsMapper statisticsMapper;

    @Autowired
    private RepairRecordMapper repairRecordMapper;

    @Autowired
    private ComplaintRecordMapper complaintRecordMapper;

    @Override
    public StatisticsVO getStatistics() {
        StatisticsVO vo = new StatisticsVO();

        // 账单统计
        Map<String, Object> bill = statisticsMapper.selectBillStatistics();
        BigDecimal monthReceivable = toDecimal(bill.get("monthReceivable"));
        BigDecimal lastMonthReceivable = toDecimal(bill.get("lastMonthReceivable"));
        vo.setMonthReceivable(monthReceivable);
        vo.setLastMonthReceivable(lastMonthReceivable);
        vo.setMonthReceived(toDecimal(bill.get("monthReceived")));
        vo.setUnpaidCount(toInt(bill.get("unpaidCount")));
        vo.setOverdueCount(toInt(bill.get("overdueCount")));
        vo.setUnpaidMonthCount(toInt(bill.get("unpaidMonthCount")));
        vo.setUnpaidAmount(toDecimal(bill.get("unpaidAmount")));

        // 缴费率 = 本月实收 / 本月应收
        if (monthReceivable.compareTo(BigDecimal.ZERO) > 0) {
            vo.setPaidRate(vo.getMonthReceived()
                    .multiply(new BigDecimal("100"))
                    .divide(monthReceivable, 1, RoundingMode.HALF_UP));
        } else {
            vo.setPaidRate(BigDecimal.ZERO);
        }

        // 应收较上月增幅
        if (lastMonthReceivable.compareTo(BigDecimal.ZERO) > 0) {
            vo.setReceivableGrowth(monthReceivable.subtract(lastMonthReceivable)
                    .multiply(new BigDecimal("100"))
                    .divide(lastMonthReceivable, 1, RoundingMode.HALF_UP));
        } else {
            vo.setReceivableGrowth(BigDecimal.ZERO);
        }

        // 报修统计
        Map<String, Object> repair = statisticsMapper.selectRepairStatistics();
        vo.setPendingRepairCount(toInt(repair.get("pendingCount")));
        vo.setRepairDispatchCount(toInt(repair.get("dispatchCount")));
        vo.setRepairHandlingCount(toInt(repair.get("handlingCount")));

        // 投诉统计
        Map<String, Object> complaint = statisticsMapper.selectComplaintStatistics();
        vo.setPendingComplaintCount(toInt(complaint.get("pendingCount")));
        vo.setComplaintReplyCount(toInt(complaint.get("replyCount")));
        vo.setComplaintHandlingCount(toInt(complaint.get("handlingCount")));

        // 楼栋/房屋统计
        Map<String, Object> house = statisticsMapper.selectHouseStatistics();
        int houseCount = toInt(house.get("houseCount"));
        int occupiedCount = toInt(house.get("occupiedCount"));
        vo.setBuildingCount(toInt(house.get("buildingCount")));
        vo.setHouseCount(houseCount);
        vo.setOccupiedCount(occupiedCount);
        vo.setVacantCount(toInt(house.get("vacantCount")));
        if (houseCount > 0) {
            vo.setOccupancyRate(new BigDecimal(occupiedCount)
                    .multiply(new BigDecimal("100"))
                    .divide(new BigDecimal(houseCount), 1, RoundingMode.HALF_UP));
        } else {
            vo.setOccupancyRate(BigDecimal.ZERO);
        }

        // 居民统计
        Map<String, Object> resident = statisticsMapper.selectResidentStatistics();
        vo.setResidentCount(toInt(resident.get("residentCount")));
        vo.setResidentMonthCount(toInt(resident.get("residentMonthCount")));

        return vo;
    }

    @Override
    public FeeChartVO getFeeChart() {
        // 生成近6个月月份序列
        List<String> months = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM");
        YearMonth current = YearMonth.now();
        for (int i = 5; i >= 0; i--) {
            months.add(current.minusMonths(i).format(fmt));
        }

        // 查询结果转 Map（月份 -> 金额）
        Map<String, BigDecimal> receivableMap = toAmountMap(statisticsMapper.selectMonthReceivable());
        Map<String, BigDecimal> receivedMap = toAmountMap(statisticsMapper.selectMonthReceived());

        FeeChartVO vo = new FeeChartVO();
        vo.setMonths(months);
        vo.setReceivable(months.stream().map(m -> receivableMap.getOrDefault(m, BigDecimal.ZERO)).collect(Collectors.toList()));
        vo.setReceived(months.stream().map(m -> receivedMap.getOrDefault(m, BigDecimal.ZERO)).collect(Collectors.toList()));
        return vo;
    }

    @Override
    public WorkOrderChartVO getWorkOrderChart() {
        Map<Integer, Integer> repairMap = toCountMap(statisticsMapper.selectRepairStatusCounts());
        Map<Integer, Integer> complaintMap = toCountMap(statisticsMapper.selectComplaintStatusCounts());

        WorkOrderChartVO vo = new WorkOrderChartVO();
        vo.setCategories(new ArrayList<>(Arrays.asList("报修单", "投诉建议")));
        // 待处理：报修-待派单(1)，投诉-待处理(1)
        vo.setPending(new ArrayList<>(Arrays.asList(
                repairMap.getOrDefault(1, 0), complaintMap.getOrDefault(1, 0))));
        // 已派单：报修-已派单(2)，投诉无此状态
        vo.setDispatched(new ArrayList<>(Arrays.asList(
                repairMap.getOrDefault(2, 0), 0)));
        // 处理中：报修-处理中(3)，投诉-处理中(2)
        vo.setHandling(new ArrayList<>(Arrays.asList(
                repairMap.getOrDefault(3, 0), complaintMap.getOrDefault(2, 0))));
        return vo;
    }

    @Override
    public List<HomeBillVO> getUnpaidBills(Integer limit) {
        return statisticsMapper.selectUnpaidBills(limit == null ? 10 : limit);
    }

    @Override
    public List<HomeBillVO> getRecentPaidBills(Integer limit) {
        return statisticsMapper.selectRecentPaidBills(limit == null ? 10 : limit);
    }

    @Override
    public List<RepairRecord> getLatestRepairs(Integer limit) {
        return repairRecordMapper.selectList(new QueryWrapper<RepairRecord>()
                .orderByDesc("create_time")
                .last("LIMIT " + (limit == null ? 10 : limit)));
    }

    @Override
    public List<ComplaintRecord> getLatestComplaints(Integer limit) {
        return complaintRecordMapper.selectList(new QueryWrapper<ComplaintRecord>()
                .orderByDesc("submit_time")
                .last("LIMIT " + (limit == null ? 10 : limit)));
    }

    /**
     * Object 转 BigDecimal
     */
    private BigDecimal toDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(value.toString());
    }

    /**
     * Object 转 int
     */
    private int toInt(Object value) {
        if (value == null) {
            return 0;
        }
        return Integer.parseInt(value.toString());
    }

    /**
     * 月份金额列表转 Map（SQL返回 yyyy-MM，截取为 MM 与图表月份对齐）
     */
    private Map<String, BigDecimal> toAmountMap(List<Map<String, Object>> list) {
        Map<String, BigDecimal> map = new HashMap<>();
        for (Map<String, Object> row : list) {
            String month = String.valueOf(row.get("month"));
            if (month.length() > 2) {
                month = month.substring(month.length() - 2);
            }
            map.put(month, toDecimal(row.get("amount")));
        }
        return map;
    }

    /**
     * 状态数量列表转 Map
     */
    private Map<Integer, Integer> toCountMap(List<Map<String, Object>> list) {
        Map<Integer, Integer> map = new HashMap<>();
        for (Map<String, Object> row : list) {
            map.put(toInt(row.get("status")), toInt(row.get("count")));
        }
        return map;
    }
}
