package com.community.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.backend.dto.CustomerServiceQueryDTO;
import com.community.backend.mapper.CustomerServiceMapper;
import com.community.backend.service.CustomerServiceService;
import com.community.common.pojo.CustomerService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 客服Service实现类
 */
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerServiceMapper, CustomerService> implements CustomerServiceService {

    @Override
    public IPage<CustomerService> selectCustomerServicePage(CustomerServiceQueryDTO queryDTO) {
        Page<CustomerService> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<CustomerService> wrapper = new LambdaQueryWrapper<>();

        // 客服姓名模糊查询
        if (StringUtils.hasText(queryDTO.getCsName())) {
            wrapper.like(CustomerService::getCsName, queryDTO.getCsName());
        }

        // 职位模糊查询
        if (StringUtils.hasText(queryDTO.getPosition())) {
            wrapper.like(CustomerService::getPosition, queryDTO.getPosition());
        }

        // 联系电话模糊查询
        if (StringUtils.hasText(queryDTO.getPhone())) {
            wrapper.like(CustomerService::getPhone, queryDTO.getPhone());
        }

        // 微信号模糊查询
        if (StringUtils.hasText(queryDTO.getWechat())) {
            wrapper.like(CustomerService::getWechat, queryDTO.getWechat());
        }

        wrapper.orderByAsc(CustomerService::getCsId);

        return page(page, wrapper);
    }

    @Override
    public CustomerService getCustomerServiceById(Integer csId) {
        return getById(csId);
    }
}
