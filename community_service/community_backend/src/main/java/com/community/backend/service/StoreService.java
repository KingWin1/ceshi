package com.community.backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.community.backend.dto.StoreQueryDTO;
import com.community.backend.vo.StoreVO;
import com.community.common.pojo.Store;

/**
 * 门店Service接口
 */
public interface StoreService extends IService<Store> {

    /**
     * 分页+条件查询门店列表
     * @param queryDTO 查询参数
     * @return 分页结果
     */
    IPage<Store> selectStorePage(StoreQueryDTO queryDTO);

    /**
     * 根据ID查询门店信息
     * @param storeId 门店ID
     * @return 门店对象
     */
    Store getStoreById(Integer storeId);

    /**
     * 根据门店ID查询门店详情（含分类名称）
     * @param storeId 门店ID
     * @return 门店详情VO
     */
    StoreVO getStoreDetail(Integer storeId);
}
