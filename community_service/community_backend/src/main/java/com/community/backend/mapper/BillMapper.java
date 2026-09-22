package com.community.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.backend.dto.BillQueryDTO;
import com.community.backend.vo.BillVO;
import com.community.common.pojo.Bill;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 账单Mapper接口
 */
@Mapper
public interface BillMapper extends BaseMapper<Bill> {

    /**
     * 分页+条件查询账单列表（7表联查）
     * @param page 分页对象
     * @param queryDTO 查询参数
     * @return 分页结果
     */
    IPage<BillVO> selectBillPage(Page<BillVO> page, @Param("query") BillQueryDTO queryDTO);

    /**
     * 根据账单ID查询账单详情（7表联查）
     * @param billId 账单ID
     * @return 账单详情
     */
    BillVO selectBillDetail(@Param("billId") Integer billId);

    /**
     * 条件查询账单列表（不分页，用于导出）
     * @param queryDTO 查询参数
     * @return 账单列表
     */
    List<BillVO> selectBillList(@Param("query") BillQueryDTO queryDTO);
}
