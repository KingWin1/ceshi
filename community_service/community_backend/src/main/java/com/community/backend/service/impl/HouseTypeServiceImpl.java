package com.community.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.backend.mapper.HouseTypeMapper;
import com.community.backend.service.HouseTypeService;
import com.community.common.pojo.HouseType;
import org.springframework.stereotype.Service;

/**
 * 户型Service实现类
 */
@Service
public class HouseTypeServiceImpl extends ServiceImpl<HouseTypeMapper, HouseType> implements HouseTypeService {

    @Override
    public Integer resolveHouseTypeId(Integer rooms, Integer hall, Integer toilet) {
        HouseType houseType = baseMapper.selectByLayout(rooms, hall, toilet);
        if (houseType != null) {
            return houseType.getHouseTypeId();
        }
        // 户型表中不存在该组合时自动新增，保证房屋保存不因户型缺失而失败
        HouseType newType = new HouseType();
        newType.setRooms(rooms);
        newType.setHall(hall);
        newType.setToilet(toilet);
        save(newType);
        return newType.getHouseTypeId();
    }
}
