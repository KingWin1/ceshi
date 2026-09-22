package com.community.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.community.common.pojo.StoreCategory;

import java.util.List;

/**
 * 门店分类Service接口
 */
public interface StoreCategoryService extends IService<StoreCategory> {

    /**
     * 查询全部的门店分类信息
     * @return 门店分类集合
     */
    List<StoreCategory> listAll();
}
