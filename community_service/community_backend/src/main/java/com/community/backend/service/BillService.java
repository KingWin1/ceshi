package com.community.backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.community.backend.dto.BillAddDTO;
import com.community.backend.dto.BillImportRowDTO;
import com.community.backend.dto.BillQueryDTO;
import com.community.backend.vo.BillVO;
import com.community.common.pojo.Bill;

import java.util.List;

/**
 * 账单Service接口
 */
public interface BillService extends IService<Bill> {

    /**
     * 分页+条件查询账单列表（7表联查）
     * @param queryDTO 查询参数
     * @return 分页结果
     */
    IPage<BillVO> selectBillPage(BillQueryDTO queryDTO);

    /**
     * 根据账单ID查询账单详情（7表联查）
     * @param billId 账单ID
     * @return 账单详情
     */
    BillVO getBillDetail(Integer billId);

    /**
     * 条件查询账单列表（不分页，用于导出）
     * @param queryDTO 查询参数
     * @return 账单列表
     */
    List<BillVO> getBillList(BillQueryDTO queryDTO);

    /**
     * 添加账单
     * @param addDTO 添加参数
     */
    void addBill(BillAddDTO addDTO);

    /**
     * 批量添加账单
     * @param addDTOList 添加参数集合
     */
    void batchAddBill(List<BillAddDTO> addDTOList);

    /**
     * 根据账单ID缴费
     * @param billId 账单ID
     * @param payMethod 支付方式
     * @param paySerialNo 支付流水号
     */
    void payBill(Integer billId, Integer payMethod, String paySerialNo);

    /**
     * 导入账单：解析楼栋/单元/房号匹配房屋，费用类型名称匹配费用类型，批量添加
     * @param rowList 导入行数据集合
     * @param overwrite 是否覆盖重复账单（同房屋+同费用类型+同计费周期）
     * @return 导入结果提示
     */
    String importBills(List<BillImportRowDTO> rowList, boolean overwrite);
}
