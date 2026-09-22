package com.community.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.backend.dto.StoreQueryDTO;
import com.community.backend.mapper.StoreMapper;
import com.community.backend.service.StoreService;
import com.community.backend.vo.StoreVO;
import com.community.common.pojo.Store;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 门店Service实现类
 */
@Service
public class StoreServiceImpl extends ServiceImpl<StoreMapper, Store> implements StoreService {

    @Override
    public IPage<Store> selectStorePage(StoreQueryDTO queryDTO) {
        Page<Store> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<Store> wrapper = new LambdaQueryWrapper<>();

        // 门店ID精确查询
        if (queryDTO.getStoreId() != null) {
            wrapper.eq(Store::getStoreId, queryDTO.getStoreId());
        }

        // 门店名称模糊查询
        if (StringUtils.hasText(queryDTO.getStoreName())) {
            wrapper.like(Store::getStoreName, queryDTO.getStoreName());
        }

        // 详细地址模糊查询
        if (StringUtils.hasText(queryDTO.getAddress())) {
            wrapper.like(Store::getAddress, queryDTO.getAddress());
        }

        // 联系电话模糊查询
        if (StringUtils.hasText(queryDTO.getContactPhone())) {
            wrapper.like(Store::getContactPhone, queryDTO.getContactPhone());
        }

        // 门店介绍模糊查询
        if (StringUtils.hasText(queryDTO.getIntroduction())) {
            wrapper.like(Store::getIntroduction, queryDTO.getIntroduction());
        }

        // 关键词：名称/地址/电话/介绍 模糊匹配（OR 语义）
        if (StringUtils.hasText(queryDTO.getKeyword())) {
            String keyword = queryDTO.getKeyword();
            wrapper.and(w -> w.like(Store::getStoreName, keyword)
                    .or().like(Store::getAddress, keyword)
                    .or().like(Store::getContactPhone, keyword)
                    .or().like(Store::getIntroduction, keyword));
        }

        // 分类ID精确查询
        if (queryDTO.getCategoryId() != null) {
            wrapper.eq(Store::getCategoryId, queryDTO.getCategoryId());
        }

        // 状态精确查询
        if (queryDTO.getStatus() != null) {
            wrapper.eq(Store::getStatus, queryDTO.getStatus());
        }

        // 推荐精确查询
        if (queryDTO.getIsRecommend() != null) {
            wrapper.eq(Store::getIsRecommend, queryDTO.getIsRecommend());
        }

        wrapper.orderByAsc(Store::getStoreId);

        return page(page, wrapper);
    }

    @Override
    public Store getStoreById(Integer storeId) {
        return getById(storeId);
    }

    @Override
    public StoreVO getStoreDetail(Integer storeId) {
        return baseMapper.selectStoreDetail(storeId);
    }
}
