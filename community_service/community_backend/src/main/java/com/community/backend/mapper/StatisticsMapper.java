package com.community.backend.mapper;

import com.community.backend.vo.HomeBillVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 首页统计Mapper接口
 */
@Mapper
public interface StatisticsMapper {

    /**
     * 账单统计（本月/上月应收、本月实收、待缴笔数、逾期笔数、本月新增待缴、未收金额）
     */
    Map<String, Object> selectBillStatistics();

    /**
     * 报修统计（待处理、待派单、处理中）
     */
    Map<String, Object> selectRepairStatistics();

    /**
     * 投诉统计（待处理、待回复、处理中）
     */
    Map<String, Object> selectComplaintStatistics();

    /**
     * 楼栋/房屋统计（楼栋数、房屋数、已入住数、空置数）
     */
    Map<String, Object> selectHouseStatistics();

    /**
     * 居民统计（总数、本月新增）
     */
    Map<String, Object> selectResidentStatistics();

    /**
     * 近6个月各月应收总额（month, amount）
     */
    List<Map<String, Object>> selectMonthReceivable();

    /**
     * 近6个月各月实收金额（month, amount）
     */
    List<Map<String, Object>> selectMonthReceived();

    /**
     * 报修单按状态数量（status, count）
     */
    List<Map<String, Object>> selectRepairStatusCounts();

    /**
     * 投诉建议按状态数量（status, count）
     */
    List<Map<String, Object>> selectComplaintStatusCounts();

    /**
     * 待缴账单列表（联查费用类型、楼栋）
     */
    List<HomeBillVO> selectUnpaidBills(@Param("limit") Integer limit);

    /**
     * 最近缴费账单列表（联查费用类型、楼栋）
     */
    List<HomeBillVO> selectRecentPaidBills(@Param("limit") Integer limit);
}
