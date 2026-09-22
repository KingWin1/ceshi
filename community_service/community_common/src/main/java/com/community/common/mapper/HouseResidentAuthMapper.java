package com.community.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.dto.HouseResidentAuthQueryDTO;
import com.community.common.vo.HouseAuthCountVO;
import com.community.common.vo.HouseResidentAuthVO;
import com.community.common.vo.ResidentAuthCountVO;
import com.community.common.pojo.HouseResidentAuth;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 房屋居民认证Mapper接口
 */
@Mapper
public interface HouseResidentAuthMapper extends BaseMapper<HouseResidentAuth> {

    /**
     * 批量添加房屋居民认证信息
     * @param residentId 居民ID
     * @param houseIds 房屋ID数组
     * @param type 认证类型：1-业主 2-家属 3-租户
     */
    void batchInsert(@Param("residentId") Integer residentId,
                     @Param("houseIds") List<Integer> houseIds,
                     @Param("type") Integer type);

    /**
     * 根据居民ID查询对应的认证信息集合
     * @param residentId 居民ID
     * @return 认证信息集合
     */
    List<HouseResidentAuth> selectByResidentId(@Param("residentId") Integer residentId);

    /**
     * 根据居民ID删除对应信息
     * @param residentId 居民ID
     */
    void deleteByResidentId(@Param("residentId") Integer residentId);

    /**
     * 分页+条件查询认证列表（5表联查：认证表-房屋表-楼栋表-居民表-管理员表）
     * @param page 分页对象
     * @param queryDTO 查询参数
     * @return 分页结果
     */
    IPage<HouseResidentAuthVO> selectAuthPage(Page<HouseResidentAuthVO> page,
                                               @Param("query") HouseResidentAuthQueryDTO queryDTO);

    /**
     * 根据认证ID数组批量修改审核状态
     * @param authIds 认证ID数组
     * @param auditStatus 审核状态
     * @param auditRemark 审核备注
     * @param adminId 管理员ID
     */
    void batchUpdateAuditStatus(@Param("authIds") List<Integer> authIds,
                                @Param("auditStatus") Integer auditStatus,
                                @Param("auditRemark") String auditRemark,
                                @Param("adminId") Integer adminId);

    /**
     * 根据房屋ID统计该房屋在认证表中出现的次数
     * @param houseId 房屋ID
     * @return 认证次数
     */
    int countByHouseId(@Param("houseId") Integer houseId);

    /**
     * 根据房屋ID数组统计每个房屋在认证表中出现的次数（联查房屋、楼栋信息）
     * @param houseIds 房屋ID数组
     * @return 每个房屋的认证统计
     */
    List<HouseAuthCountVO> countByHouseIds(@Param("houseIds") List<Integer> houseIds);

    /**
     * 根据房屋ID删除认证表中该房屋的认证数据
     * @param houseId 房屋ID
     */
    void deleteByHouseId(@Param("houseId") Integer houseId);

    /**
     * 根据房屋ID数组批量删除认证表数据
     * @param houseIds 房屋ID数组
     */
    void deleteByHouseIds(@Param("houseIds") List<Integer> houseIds);

    /**
     * 根据房屋ID数组批量查询认证表数据
     * @param houseIds 房屋ID数组
     * @return 认证信息集合
     */
    List<HouseResidentAuth> selectListByHouseIds(@Param("houseIds") List<Integer> houseIds);

    /**
     * 根据居民ID统计该居民在认证表中出现的次数
     * @param residentId 居民ID
     * @return 认证次数
     */
    int countByResidentId(@Param("residentId") Integer residentId);

    /**
     * 根据居民ID数组统计每个居民在认证表中出现的次数（联查居民信息）
     * @param residentIds 居民ID数组
     * @return 每个居民的认证统计
     */
    List<ResidentAuthCountVO> countByResidentIds(@Param("residentIds") List<Integer> residentIds);

    /**
     * 根据居民ID数组批量删除认证表数据
     * @param residentIds 居民ID数组
     */
    void deleteByResidentIds(@Param("residentIds") List<Integer> residentIds);

    /**
     * 根据居民ID数组批量查询认证表数据
     * @param residentIds 居民ID数组
     * @return 认证信息集合
     */
    List<HouseResidentAuth> selectListByResidentIds(@Param("residentIds") List<Integer> residentIds);
}
