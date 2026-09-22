package com.community.backend.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.common.dto.HouseQueryDTO;
import com.community.backend.dto.HouseSaveDTO;
import com.community.common.mapper.HouseMapper;
import com.community.common.mapper.HouseResidentAuthMapper;
import com.community.backend.service.HouseService;
import com.community.backend.service.HouseTypeService;
import com.community.common.vo.HouseDetailVO;
import com.community.common.vo.HouseVO;
import com.community.common.pojo.House;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 房屋Service实现类
 */
@Service
public class HouseServiceImpl extends ServiceImpl<HouseMapper, House> implements HouseService {

    @Autowired
    private HouseTypeService houseTypeService;

    @Autowired
    private HouseResidentAuthMapper houseResidentAuthMapper;

    @Override
    public IPage<HouseVO> selectHousePage(HouseQueryDTO queryDTO) {
        Page<HouseVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        return baseMapper.selectHousePage(page, queryDTO);
    }

    @Override
    public List<House> listByBuildingId(Integer buildingId) {
        return baseMapper.selectByBuildingId(buildingId);
    }

    @Override
    public List<House> listByBuildingIdAndUnitNo(Integer buildingId, String unitNo) {
        return baseMapper.selectByBuildingIdAndUnitNo(buildingId, unitNo);
    }

    @Override
    public List<House> listByIds(List<Integer> houseIds) {
        return baseMapper.selectHouseListByIds(houseIds);
    }

    @Override
    public List<Integer> selectHouseIds(Integer buildingId, String unitNo, String houseNumber) {
        return baseMapper.selectHouseIdsByBuildingIdAndUnitNoAndHouseNumber(buildingId, unitNo, houseNumber);
    }

    @Override
    public List<House> listAll() {
        return baseMapper.selectAll();
    }

    @Override
    public List<HouseVO> listByCondition(Integer buildingId, String unitNo, String keyword) {
        HouseQueryDTO queryDTO = new HouseQueryDTO();
        queryDTO.setBuildingId(buildingId);
        queryDTO.setUnitNo(unitNo);
        queryDTO.setKeyword(keyword);
        Page<HouseVO> page = new Page<>(1, 1000);
        IPage<HouseVO> result = baseMapper.selectHousePage(page, queryDTO);
        return result.getRecords();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveHouse(HouseSaveDTO saveDTO) {
        House house = new House();
        copyToHouse(saveDTO, house);
        house.setHouseTypeId(resolveTypeId(saveDTO));
        save(house);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateHouse(HouseSaveDTO saveDTO) {
        House house = new House();
        house.setHouseId(saveDTO.getHouseId());
        copyToHouse(saveDTO, house);
        house.setHouseTypeId(resolveTypeId(saveDTO));
        updateById(house);
    }

    @Override
    public HouseDetailVO getHouseDetail(Integer houseId) {
        return baseMapper.selectHouseDetailById(houseId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteHouse(Integer houseId) {
        // 先删除该房屋的认证记录，再删除房屋信息
        houseResidentAuthMapper.deleteByHouseId(houseId);
        baseMapper.deleteById(houseId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteHouseBatch(List<Integer> houseIds) {
        if (houseIds == null || houseIds.isEmpty()) {
            return;
        }
        // 先批量删除房屋的认证记录，再批量删除房屋信息
        houseResidentAuthMapper.deleteByHouseIds(houseIds);
        baseMapper.deleteBatchIds(houseIds);
    }

    /**
     * DTO 基础字段拷贝到实体
     */
    private void copyToHouse(HouseSaveDTO saveDTO, House house) {
        house.setBuildingId(saveDTO.getBuildingId());
        house.setUnitNo(saveDTO.getUnitNo());
        house.setHouseNumber(saveDTO.getHouseNumber());
        house.setArea(saveDTO.getArea());
        house.setRoomType(saveDTO.getRoomType());
        house.setOrientation(saveDTO.getOrientation());
        house.setStatus(saveDTO.getStatus());
    }

    /**
     * 根据室/厅/卫解析户型ID，未传户型时默认按 0室0厅0卫 匹配
     */
    private Integer resolveTypeId(HouseSaveDTO saveDTO) {
        int rooms = saveDTO.getRooms() == null ? 0 : saveDTO.getRooms();
        int hall = saveDTO.getHall() == null ? 0 : saveDTO.getHall();
        int toilet = saveDTO.getToilet() == null ? 0 : saveDTO.getToilet();
        return houseTypeService.resolveHouseTypeId(rooms, hall, toilet);
    }
}
