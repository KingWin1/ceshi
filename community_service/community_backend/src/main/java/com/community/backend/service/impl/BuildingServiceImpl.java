package com.community.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.backend.dto.BuildingQueryDTO;
import com.community.backend.mapper.BuildingMapper;
import com.community.common.mapper.HouseMapper;
import com.community.backend.service.BuildingService;
import com.community.common.exception.BusinessException;
import com.community.common.pojo.Building;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 楼栋Service实现类
 */
@Service
public class BuildingServiceImpl extends ServiceImpl<BuildingMapper, Building> implements BuildingService {

    @Autowired
    private HouseMapper houseMapper;

    @Override
    public IPage<Building> selectBuildingPage(BuildingQueryDTO queryDTO) {
        Page<Building> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<Building> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(queryDTO.getBuildingName())) {
            wrapper.like(Building::getBuildingName, queryDTO.getBuildingName());
        }
        wrapper.orderByAsc(Building::getBuildingId);

        return page(page, wrapper);
    }

    @Override
    public Building getBuildingById(Integer buildingId) {
        return getById(buildingId);
    }

    @Override
    public void deleteBuilding(Integer buildingId) {
        // 校验楼栋是否被房屋占用，被占用则不允许删除
        int houseCount = houseMapper.countByBuildingId(buildingId);
        if (houseCount > 0) {
            throw new BusinessException("该楼栋被房屋占用不能删除");
        }
        removeById(buildingId);
    }

    @Override
    public Map<String, Object> deleteBuildingBatch(List<Integer> buildingIds) {
        Map<String, Object> result = new HashMap<>();
        result.put("deletedCount", 0);
        result.put("occupiedNames", new ArrayList<String>());
        if (buildingIds == null || buildingIds.isEmpty()) {
            return result;
        }
        // 查询被房屋占用的楼栋，从待删除集合中剔除
        List<Integer> occupiedIds = houseMapper.selectOccupiedBuildingIds(buildingIds);
        List<String> occupiedNames = new ArrayList<>();
        if (!occupiedIds.isEmpty()) {
            List<Building> occupiedBuildings = listByIds(occupiedIds);
            for (Building building : occupiedBuildings) {
                occupiedNames.add(building.getBuildingName());
            }
        }
        Set<Integer> occupiedSet = new HashSet<>(occupiedIds);
        List<Integer> deletableIds = new ArrayList<>();
        for (Integer buildingId : buildingIds) {
            if (!occupiedSet.contains(buildingId)) {
                deletableIds.add(buildingId);
            }
        }
        if (!deletableIds.isEmpty()) {
            removeByIds(deletableIds);
        }
        result.put("deletedCount", deletableIds.size());
        result.put("occupiedNames", occupiedNames);
        return result;
    }
}
