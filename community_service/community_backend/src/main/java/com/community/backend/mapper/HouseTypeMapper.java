package com.community.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.common.pojo.HouseType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 户型Mapper接口
 */
@Mapper
public interface HouseTypeMapper extends BaseMapper<HouseType> {

    /**
     * 根据室、厅、卫的个数查询匹配的户型
     * @param rooms 室
     * @param hall 厅
     * @param toilet 卫
     * @return 户型信息，不存在返回null
     */
    HouseType selectByLayout(@Param("rooms") Integer rooms,
                             @Param("hall") Integer hall,
                             @Param("toilet") Integer toilet);
}
