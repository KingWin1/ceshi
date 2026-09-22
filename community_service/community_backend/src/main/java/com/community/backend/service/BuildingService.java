package com.community.backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.community.backend.dto.BuildingQueryDTO;
import com.community.common.pojo.Building;

import java.util.List;
import java.util.Map;

/**
 * 楼栋Service接口
 */
public interface BuildingService extends IService<Building> {

    /**
     * 分页+条件查询楼栋列表
     * @param queryDTO 查询参数
     * @return 分页结果
     */
    IPage<Building> selectBuildingPage(BuildingQueryDTO queryDTO);

    /**
     * 根据ID查询楼栋信息
     * @param buildingId 楼栋ID
     * @return 楼栋对象
     */
    Building getBuildingById(Integer buildingId);

    /**
     * 删除楼栋信息（被房屋占用时不允许删除）
     * @param buildingId 楼栋ID
     */
    void deleteBuilding(Integer buildingId);

    /**
     * 批量删除楼栋信息（自动跳过被房屋占用的楼栋，仅删除未占用楼栋）
     * @param buildingIds 楼栋ID数组
     * @return deletedCount-实际删除数量，occupiedNames-被占用未删除的楼栋名称集合
     */
    Map<String, Object> deleteBuildingBatch(List<Integer> buildingIds);
}
