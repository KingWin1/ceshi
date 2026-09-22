package com.community.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.common.pojo.CustomerService;
import org.apache.ibatis.annotations.Mapper;

/**
 * 客服Mapper接口
 */
@Mapper
public interface CustomerServiceMapper extends BaseMapper<CustomerService> {
}
