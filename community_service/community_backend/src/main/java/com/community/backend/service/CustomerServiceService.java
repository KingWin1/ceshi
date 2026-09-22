package com.community.backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.community.backend.dto.CustomerServiceQueryDTO;
import com.community.common.pojo.CustomerService;

/**
 * 客服Service接口
 */
public interface CustomerServiceService extends IService<CustomerService> {

    /**
     * 分页+条件查询客服列表
     * @param queryDTO 查询参数
     * @return 分页结果
     */
    IPage<CustomerService> selectCustomerServicePage(CustomerServiceQueryDTO queryDTO);

    /**
     * 根据ID查询客服信息
     * @param csId 客服ID
     * @return 客服对象
     */
    CustomerService getCustomerServiceById(Integer csId);
}
