package com.community.backend.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.backend.dto.HouseResidentAuthAddDTO;
import com.community.common.dto.HouseResidentAuthQueryDTO;
import com.community.backend.dto.HouseResidentAuthUpdateDTO;
import com.community.common.mapper.HouseResidentAuthMapper;
import com.community.backend.service.HouseResidentAuthService;
import com.community.common.vo.HouseAuthCountVO;
import com.community.common.vo.HouseResidentAuthVO;
import com.community.common.vo.ResidentAuthCountVO;
import com.community.common.pojo.HouseResidentAuth;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 房屋居民认证Service实现类
 */
@Service
public class HouseResidentAuthServiceImpl extends ServiceImpl<HouseResidentAuthMapper, HouseResidentAuth> implements HouseResidentAuthService {

    @Override
    public IPage<HouseResidentAuthVO> selectAuthPage(HouseResidentAuthQueryDTO queryDTO) {
        Page<HouseResidentAuthVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        return baseMapper.selectAuthPage(page, queryDTO);
    }

    @Override
    public void updateAuditStatus(Integer authId, Integer auditStatus, String auditRemark, Integer adminId) {
        HouseResidentAuth auth = new HouseResidentAuth();
        auth.setAuthId(authId);
        auth.setAuditStatus(auditStatus);
        auth.setAuditRemark(auditRemark);
        auth.setAdminId(adminId);
        baseMapper.updateById(auth);
    }

    @Override
    public void batchUpdateAuditStatus(List<Integer> authIds, Integer auditStatus, String auditRemark, Integer adminId) {
        baseMapper.batchUpdateAuditStatus(authIds, auditStatus, auditRemark, adminId);
    }

    @Override
    public void addAuth(HouseResidentAuthAddDTO addDTO) {
        HouseResidentAuth auth = new HouseResidentAuth();
        auth.setHouseId(addDTO.getHouseId());
        auth.setResidentId(addDTO.getResidentId());
        auth.setType(addDTO.getType());
        auth.setAuthMaterialUrl(addDTO.getAuthMaterialUrl());
        auth.setAuditStatus(addDTO.getAuditStatus());
        auth.setAuditRemark(addDTO.getAuditRemark());
        auth.setAdminId(addDTO.getAdminId());
        auth.setIsMain(addDTO.getIsMain());
        baseMapper.insert(auth);
    }

    @Override
    public HouseResidentAuth getAuthById(Integer authId) {
        return baseMapper.selectById(authId);
    }

    @Override
    public void updateAuth(HouseResidentAuthUpdateDTO updateDTO) {
        HouseResidentAuth auth = new HouseResidentAuth();
        auth.setAuthId(updateDTO.getAuthId());
        auth.setHouseId(updateDTO.getHouseId());
        auth.setResidentId(updateDTO.getResidentId());
        auth.setType(updateDTO.getType());
        auth.setAuthMaterialUrl(updateDTO.getAuthMaterialUrl());
        auth.setAuditStatus(updateDTO.getAuditStatus());
        auth.setAuditRemark(updateDTO.getAuditRemark());
        auth.setAdminId(updateDTO.getAdminId());
        auth.setIsMain(updateDTO.getIsMain());
        baseMapper.updateById(auth);
    }

    @Override
    public int countByHouseId(Integer houseId) {
        return baseMapper.countByHouseId(houseId);
    }

    @Override
    public List<HouseAuthCountVO> countAuthByHouseIds(List<Integer> houseIds) {
        if (houseIds == null || houseIds.isEmpty()) {
            return Collections.emptyList();
        }
        return baseMapper.countByHouseIds(houseIds);
    }

    @Override
    public List<HouseResidentAuth> listByHouseIds(List<Integer> houseIds) {
        if (houseIds == null || houseIds.isEmpty()) {
            return Collections.emptyList();
        }
        return baseMapper.selectListByHouseIds(houseIds);
    }

    @Override
    public int countByResidentId(Integer residentId) {
        return baseMapper.countByResidentId(residentId);
    }

    @Override
    public List<ResidentAuthCountVO> countAuthByResidentIds(List<Integer> residentIds) {
        if (residentIds == null || residentIds.isEmpty()) {
            return Collections.emptyList();
        }
        return baseMapper.countByResidentIds(residentIds);
    }

    @Override
    public List<HouseResidentAuth> listByResidentIds(List<Integer> residentIds) {
        if (residentIds == null || residentIds.isEmpty()) {
            return Collections.emptyList();
        }
        return baseMapper.selectListByResidentIds(residentIds);
    }
}
