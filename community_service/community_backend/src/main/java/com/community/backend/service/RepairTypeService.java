package com.community.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.community.common.pojo.RepairType;

import java.util.List;

/**
 * 报修类型Service接口
 */
public interface RepairTypeService extends IService<RepairType> {

    /**
     * 查询全部报修类型信息
     * @return 报修类型信息集合
     */
    List<RepairType> listAll();
}
