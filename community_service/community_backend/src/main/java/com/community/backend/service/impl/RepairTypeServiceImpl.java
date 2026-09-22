package com.community.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.backend.mapper.RepairTypeMapper;
import com.community.backend.service.RepairTypeService;
import com.community.common.pojo.RepairType;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 报修类型Service实现类
 */
@Service
public class RepairTypeServiceImpl extends ServiceImpl<RepairTypeMapper, RepairType> implements RepairTypeService {

    @Override
    public List<RepairType> listAll() {
        return list();
    }
}
