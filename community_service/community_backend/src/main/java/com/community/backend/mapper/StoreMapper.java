package com.community.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.backend.vo.StoreVO;
import com.community.common.pojo.Store;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 门店Mapper接口
 */
@Mapper
public interface StoreMapper extends BaseMapper<Store> {

    /**
     * 根据门店ID查询门店详情（关联门店分类名称）
     * @param storeId 门店ID
     * @return 门店详情VO
     */
    StoreVO selectStoreDetail(@Param("storeId") Integer storeId);
}
