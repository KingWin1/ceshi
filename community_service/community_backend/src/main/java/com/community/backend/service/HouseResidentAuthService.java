package com.community.backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.community.backend.dto.HouseResidentAuthAddDTO;
import com.community.common.dto.HouseResidentAuthQueryDTO;
import com.community.backend.dto.HouseResidentAuthUpdateDTO;
import com.community.common.vo.HouseAuthCountVO;
import com.community.common.vo.HouseResidentAuthVO;
import com.community.common.vo.ResidentAuthCountVO;
import com.community.common.pojo.HouseResidentAuth;

import java.util.List;

/**
 * 房屋居民认证Service接口
 */
public interface HouseResidentAuthService extends IService<HouseResidentAuth> {

    /**
     * 分页+条件查询认证列表（5表联查）
     * @param queryDTO 查询参数
     * @return 分页结果
     */
    IPage<HouseResidentAuthVO> selectAuthPage(HouseResidentAuthQueryDTO queryDTO);

    /**
     * 根据认证ID修改审核状态
     * @param authId 认证ID
     * @param auditStatus 审核状态
     * @param auditRemark 审核备注
     * @param adminId 管理员ID
     */
    void updateAuditStatus(Integer authId, Integer auditStatus, String auditRemark, Integer adminId);

    /**
     * 根据认证ID数组批量修改审核状态
     * @param authIds 认证ID数组
     * @param auditStatus 审核状态
     * @param auditRemark 审核备注
     * @param adminId 管理员ID
     */
    void batchUpdateAuditStatus(List<Integer> authIds, Integer auditStatus, String auditRemark, Integer adminId);

    /**
     * 添加认证信息
     * @param addDTO 添加参数
     */
    void addAuth(HouseResidentAuthAddDTO addDTO);

    /**
     * 根据认证ID查询指定认证信息
     * @param authId 认证ID
     * @return 认证信息对象
     */
    HouseResidentAuth getAuthById(Integer authId);

    /**
     * 根据认证ID修改指定认证信息
     * @param updateDTO 修改参数
     */
    void updateAuth(HouseResidentAuthUpdateDTO updateDTO);

    /**
     * 根据房屋ID统计该房屋在认证表中出现的次数
     * @param houseId 房屋ID
     * @return 认证次数
     */
    int countByHouseId(Integer houseId);

    /**
     * 根据房屋ID数组统计每个房屋在认证表中出现的次数
     * @param houseIds 房屋ID数组
     * @return 每个房屋的认证统计
     */
    List<HouseAuthCountVO> countAuthByHouseIds(List<Integer> houseIds);

    /**
     * 根据房屋ID数组批量查询认证表数据
     * @param houseIds 房屋ID数组
     * @return 认证信息集合
     */
    List<HouseResidentAuth> listByHouseIds(List<Integer> houseIds);

    /**
     * 根据居民ID统计该居民在认证表中出现的次数
     * @param residentId 居民ID
     * @return 认证次数
     */
    int countByResidentId(Integer residentId);

    /**
     * 根据居民ID数组统计每个居民在认证表中出现的次数
     * @param residentIds 居民ID数组
     * @return 每个居民的认证统计
     */
    List<ResidentAuthCountVO> countAuthByResidentIds(List<Integer> residentIds);

    /**
     * 根据居民ID数组批量查询认证表数据
     * @param residentIds 居民ID数组
     * @return 认证信息集合
     */
    List<HouseResidentAuth> listByResidentIds(List<Integer> residentIds);
}
