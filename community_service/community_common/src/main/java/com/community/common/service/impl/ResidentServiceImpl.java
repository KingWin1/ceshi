package com.community.common.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.common.dto.ResidentAddDTO;
import com.community.common.dto.ResidentQueryDTO;
import com.community.common.dto.ResidentRoomItemDTO;
import com.community.common.dto.ResidentUpdateDTO;
import com.community.common.mapper.HouseMapper;
import com.community.common.mapper.HouseResidentAuthMapper;
import com.community.common.mapper.ResidentMapper;
import com.community.common.service.ResidentService;
import com.community.common.vo.ResidentVO;
import com.community.common.pojo.House;
import com.community.common.pojo.HouseResidentAuth;
import com.community.common.pojo.Resident;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 居民Service实现类
 */
@Service
public class ResidentServiceImpl extends ServiceImpl<ResidentMapper, Resident> implements ResidentService {

    @Autowired
    private ResidentMapper residentMapper;

    @Autowired
    private HouseMapper houseMapper;

    @Autowired
    private HouseResidentAuthMapper houseResidentAuthMapper;

    @Override
    public IPage<ResidentVO> selectResidentPage(ResidentQueryDTO queryDTO) {
        Page<ResidentVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        return residentMapper.selectResidentPage(page, queryDTO);
    }

    @Override
    @Transactional
    public boolean addResident(ResidentAddDTO addDTO) {
        // 1. 添加居民信息，获取新添加居民的ID
        Resident resident = new Resident();
        resident.setName(addDTO.getName());
        resident.setPhone(addDTO.getPhone());
        // 前台注册传入密码时一并保存，后台新增不传则为空，不影响原有逻辑
        resident.setPassword(addDTO.getPassword());
        resident.setIdCard(addDTO.getIdCard());
        resident.setGender(addDTO.getGender());
        resident.setType(addDTO.getType());
        resident.setRegisterWay(addDTO.getRegisterWay() != null ? addDTO.getRegisterWay() : 2);
        resident.setAvatarUrl(addDTO.getAvatarUrl());
        resident.setStatus(addDTO.getStatus() != null ? addDTO.getStatus() : 1);
        residentMapper.insert(resident);

        // 2. 逐个关联房间：按楼栋+单元+房间号查房屋ID，按该行的认证类型批量写入认证表
        saveResidentRooms(resident.getResidentId(), addDTO.getRooms());

        return true;
    }

    /**
     * 保存居民的关联房间（楼栋+单元+房间号 -> 房屋ID -> 批量写入认证表）
     * @param residentId 居民ID
     * @param rooms 关联房间列表
     */
    private void saveResidentRooms(Integer residentId, List<ResidentRoomItemDTO> rooms) {
        if (rooms == null || rooms.isEmpty()) {
            return;
        }
        for (ResidentRoomItemDTO room : rooms) {
            if (room.getBuildingId() == null || room.getHouseNumber() == null) {
                continue;
            }
            List<Integer> houseIds = houseMapper.selectHouseIdsByBuildingIdAndUnitNoAndHouseNumber(
                    room.getBuildingId(), room.getUnitNo(), room.getHouseNumber());
            if (houseIds != null && !houseIds.isEmpty()) {
                houseResidentAuthMapper.batchInsert(residentId, houseIds, room.getType());
            }
        }
    }

    @Override
    public Map<String, Object> getResidentForUpdate(Integer residentId) {
        Map<String, Object> result = new HashMap<>();

        // 1. 根据居民ID查询居民信息
        Resident resident = residentMapper.selectResidentById(residentId);
        result.put("resident", resident);

        // 2. 根据居民ID查询对应的认证信息集合
        List<HouseResidentAuth> authList = houseResidentAuthMapper.selectByResidentId(residentId);
        result.put("authList", authList);

        // 3. 根据房屋ID数组查询对应房屋信息集合
        List<House> houseList = new ArrayList<>();
        if (authList != null && !authList.isEmpty()) {
            List<Integer> houseIds = new ArrayList<>();
            for (HouseResidentAuth auth : authList) {
                houseIds.add(auth.getHouseId());
            }
            houseList = houseMapper.selectHouseListByIds(houseIds);
        }
        result.put("houseList", houseList);

        return result;
    }

    @Override
    @Transactional
    public boolean updateResident(ResidentUpdateDTO updateDTO) {
        // 1. 根据居民ID修改居民基本信息
        Resident resident = new Resident();
        resident.setResidentId(updateDTO.getResidentId());
        resident.setName(updateDTO.getName());
        resident.setPhone(updateDTO.getPhone());
        resident.setIdCard(updateDTO.getIdCard());
        resident.setGender(updateDTO.getGender());
        resident.setType(updateDTO.getType());
        resident.setStatus(updateDTO.getStatus());
        resident.setAvatarUrl(updateDTO.getAvatarUrl());
        residentMapper.updateResidentById(resident);

        // 2. 先删除该居民原有的认证记录，再按提交的关联房间重新写入
        houseResidentAuthMapper.deleteByResidentId(updateDTO.getResidentId());
        saveResidentRooms(updateDTO.getResidentId(), updateDTO.getRooms());

        return true;
    }

    @Override
    @Transactional
    public void deleteResident(Integer residentId) {
        // 先删除该居民的认证记录，再删除居民信息
        houseResidentAuthMapper.deleteByResidentId(residentId);
        residentMapper.deleteById(residentId);
    }

    @Override
    @Transactional
    public void deleteResidentBatch(List<Integer> residentIds) {
        if (residentIds == null || residentIds.isEmpty()) {
            return;
        }
        // 先批量删除居民的认证记录，再批量删除居民信息
        houseResidentAuthMapper.deleteByResidentIds(residentIds);
        residentMapper.deleteBatchIds(residentIds);
    }

    @Override
    public List<Resident> searchByNameOrPhone(String name, String phone) {
        return residentMapper.selectByNameOrPhone(name, phone);
    }
}
