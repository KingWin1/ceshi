package com.community.common.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.community.common.dto.ResidentAddDTO;
import com.community.common.dto.ResidentQueryDTO;
import com.community.common.dto.ResidentUpdateDTO;
import com.community.common.vo.ResidentVO;
import com.community.common.pojo.House;
import com.community.common.pojo.HouseResidentAuth;
import com.community.common.pojo.Resident;

import java.util.List;
import java.util.Map;

/**
 * 居民Service接口
 */
public interface ResidentService extends IService<Resident> {

    /**
     * 分页+条件查询居民列表（多表联查）
     * @param queryDTO 查询参数
     * @return 分页结果
     */
    IPage<ResidentVO> selectResidentPage(ResidentQueryDTO queryDTO);

    /**
     * 添加居民（同时关联房屋）
     * @param addDTO 添加参数
     * @return 是否成功
     */
    boolean addResident(ResidentAddDTO addDTO);

    /**
     * 修改回显：查询居民信息和该居民的房屋信息集合
     * @param residentId 居民ID
     * @return 包含resident和houseList的Map
     */
    Map<String, Object> getResidentForUpdate(Integer residentId);

    /**
     * 修改居民信息（同时更新房屋关联）
     * @param updateDTO 修改参数
     * @return 是否成功
     */
    boolean updateResident(ResidentUpdateDTO updateDTO);

    /**
     * 删除居民信息（同步删除其认证记录）
     * @param residentId 居民ID
     */
    void deleteResident(Integer residentId);

    /**
     * 批量删除居民信息（同步删除其认证记录）
     * @param residentIds 居民ID数组
     */
    void deleteResidentBatch(List<Integer> residentIds);

    /**
     * 根据姓名或电话查询指定居民信息
     * @param name 姓名
     * @param phone 电话
     * @return 居民信息集合
     */
    List<Resident> searchByNameOrPhone(String name, String phone);
}
