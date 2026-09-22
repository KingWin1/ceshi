package com.community.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.backend.mapper.StoreCategoryMapper;
import com.community.backend.service.StoreCategoryService;
import com.community.common.pojo.StoreCategory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 门店分类Service实现类
 */
@Service
public class StoreCategoryServiceImpl extends ServiceImpl<StoreCategoryMapper, StoreCategory> implements StoreCategoryService {

    @Override
    public List<StoreCategory> listAll() {
        return list();
    }
}
