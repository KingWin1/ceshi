package com.community.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.common.pojo.RepairType;
import org.apache.ibatis.annotations.Mapper;

/**
 * 报修类型Mapper接口
 */
@Mapper
public interface RepairTypeMapper extends BaseMapper<RepairType> {
}
