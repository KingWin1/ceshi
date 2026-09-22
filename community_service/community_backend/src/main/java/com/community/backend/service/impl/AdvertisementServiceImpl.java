package com.community.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.backend.dto.AdvertisementQueryDTO;
import com.community.backend.mapper.AdvertisementMapper;
import com.community.backend.service.AdvertisementService;
import com.community.common.pojo.Advertisement;
import org.springframework.stereotype.Service;

/**
 * 广告Service实现类
 */
@Service
public class AdvertisementServiceImpl extends ServiceImpl<AdvertisementMapper, Advertisement> implements AdvertisementService {

    @Override
    public IPage<Advertisement> selectAdvertisementPage(AdvertisementQueryDTO queryDTO) {
        Page<Advertisement> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<Advertisement> wrapper = new LambdaQueryWrapper<>();
        // 广告类型条件
        if (queryDTO.getAdType() != null) {
            wrapper.eq(Advertisement::getAdType, queryDTO.getAdType());
        }
        // 状态条件
        if (queryDTO.getStatus() != null) {
            wrapper.eq(Advertisement::getStatus, queryDTO.getStatus());
        }
        wrapper.orderByAsc(Advertisement::getSort);

        return page(page, wrapper);
    }

    @Override
    public void updateStatus(Integer adId, Integer status) {
        LambdaUpdateWrapper<Advertisement> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Advertisement::getAdId, adId)
               .set(Advertisement::getStatus, status);
        update(wrapper);
    }
}
