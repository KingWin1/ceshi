package com.community.backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.community.backend.dto.AdvertisementQueryDTO;
import com.community.common.pojo.Advertisement;

/**
 * 广告Service接口
 */
public interface AdvertisementService extends IService<Advertisement> {

    /**
     * 分页+条件查询广告列表
     * @param queryDTO 查询参数
     * @return 分页结果
     */
    IPage<Advertisement> selectAdvertisementPage(AdvertisementQueryDTO queryDTO);

    /**
     * 根据ID修改广告状态
     * @param adId 广告ID
     * @param status 状态
     */
    void updateStatus(Integer adId, Integer status);
}
