package com.community.backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.community.backend.dto.FeeTypeQueryDTO;
import com.community.common.pojo.FeeType;

import java.math.BigDecimal;
import java.util.List;

/**
 * 费用类型Service接口
 */
public interface FeeTypeService extends IService<FeeType> {

    /**
     * 分页+条件查询费用类型列表
     * @param queryDTO 查询参数
     * @return 分页结果
     */
    IPage<FeeType> selectFeeTypePage(FeeTypeQueryDTO queryDTO);

    /**
     * 查询全部费用类型
     * @return 费用类型集合
     */
    List<FeeType> listAll();

    /**
     * 根据费用类型ID数组查询对应的费用类型信息
     * @param feeTypeIds 费用类型ID数组
     * @return 费用类型信息集合
     */
    List<FeeType> listByIds(List<Integer> feeTypeIds);

    /**
     * 添加费用类型
     * @param feeType 费用类型对象
     */
    void addFeeType(FeeType feeType);

    /**
     * 根据费用类型ID查询指定费用类型信息
     * @param feeTypeId 费用类型ID
     * @return 费用类型对象
     */
    FeeType getById(Integer feeTypeId);

    /**
     * 根据费用类型ID修改指定费用类型信息
     * @param feeType 费用类型对象
     */
    void updateFeeType(FeeType feeType);

    /**
     * 根据费用类型ID删除指定费用类型信息（需先检查是否被账单占用）
     * @param feeTypeId 费用类型ID
     */
    void deleteFeeType(Integer feeTypeId);

    /**
     * 根据费用类型ID修改单价信息
     * @param feeTypeId 费用类型ID
     * @param monthlyPrice 按月单价
     * @param quarterlyPrice 按季度单价
     * @param yearlyPrice 按年单价
     */
    void updatePrice(Integer feeTypeId, BigDecimal monthlyPrice, BigDecimal quarterlyPrice, BigDecimal yearlyPrice);
}
