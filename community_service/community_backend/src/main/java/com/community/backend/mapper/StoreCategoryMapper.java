package com.community.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.common.pojo.StoreCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 门店分类Mapper接口
 */
@Mapper
public interface StoreCategoryMapper extends BaseMapper<StoreCategory> {
}
