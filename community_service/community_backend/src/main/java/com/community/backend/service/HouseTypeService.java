package com.community.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.community.common.pojo.HouseType;

/**
 * 户型Service接口
 */
public interface HouseTypeService extends IService<HouseType> {

    /**
     * 根据室、厅、卫的个数解析户型ID，不存在则自动新增一条户型记录
     * @param rooms 室
     * @param hall 厅
     * @param toilet 卫
     * @return 户型ID
     */
    Integer resolveHouseTypeId(Integer rooms, Integer hall, Integer toilet);
}
