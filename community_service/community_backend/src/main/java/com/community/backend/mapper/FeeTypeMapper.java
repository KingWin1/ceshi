package com.community.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.common.pojo.FeeType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 费用类型Mapper接口
 */
@Mapper
public interface FeeTypeMapper extends BaseMapper<FeeType> {

    /**
     * 根据费用类型ID统计该费用类型在账单表中使用的次数
     * @param feeTypeId 费用类型ID
     * @return 使用次数
     */
    Integer countByFeeTypeId(@Param("feeTypeId") Integer feeTypeId);
}
