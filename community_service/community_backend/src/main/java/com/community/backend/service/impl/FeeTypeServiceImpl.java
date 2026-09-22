package com.community.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.backend.dto.FeeTypeQueryDTO;
import com.community.backend.mapper.FeeTypeMapper;
import com.community.backend.service.FeeTypeService;
import com.community.common.pojo.FeeType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * 费用类型Service实现类
 */
@Service
public class FeeTypeServiceImpl extends ServiceImpl<FeeTypeMapper, FeeType> implements FeeTypeService {

    @Override
    public IPage<FeeType> selectFeeTypePage(FeeTypeQueryDTO queryDTO) {
        Page<FeeType> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<FeeType> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(queryDTO.getFeeTypeName())) {
            wrapper.like(FeeType::getFeeTypeName, queryDTO.getFeeTypeName());
        }
        wrapper.orderByAsc(FeeType::getSort).orderByAsc(FeeType::getFeeTypeId);

        return page(page, wrapper);
    }

    @Override
    public List<FeeType> listAll() {
        return list();
    }

    @Override
    public List<FeeType> listByIds(List<Integer> feeTypeIds) {
        return baseMapper.selectBatchIds(feeTypeIds);
    }

    @Override
    public void addFeeType(FeeType feeType) {
        baseMapper.insert(feeType);
    }

    @Override
    public FeeType getById(Integer feeTypeId) {
        return baseMapper.selectById(feeTypeId);
    }

    @Override
    public void updateFeeType(FeeType feeType) {
        baseMapper.updateById(feeType);
    }

    @Override
    public void deleteFeeType(Integer feeTypeId) {
        // 先检查该费用类型是否被账单占用
        Integer count = baseMapper.countByFeeTypeId(feeTypeId);
        if (count != null && count > 0) {
            throw new RuntimeException("该费用类型已被" + count + "条账单使用，无法删除");
        }
        baseMapper.deleteById(feeTypeId);
    }

    @Override
    public void updatePrice(Integer feeTypeId, BigDecimal monthlyPrice, BigDecimal quarterlyPrice, BigDecimal yearlyPrice) {
        FeeType feeType = baseMapper.selectById(feeTypeId);
        if (feeType == null) {
            throw new RuntimeException("费用类型不存在");
        }
        if (monthlyPrice != null) {
            feeType.setMonthlyPrice(monthlyPrice);
        }
        if (quarterlyPrice != null) {
            feeType.setQuarterlyPrice(quarterlyPrice);
        }
        if (yearlyPrice != null) {
            feeType.setYearlyPrice(yearlyPrice);
        }
        baseMapper.updateById(feeType);
    }
}
