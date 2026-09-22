package com.community.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.common.pojo.Advertisement;
import org.apache.ibatis.annotations.Mapper;

/**
 * 广告Mapper接口
 */
@Mapper
public interface AdvertisementMapper extends BaseMapper<Advertisement> {
}
