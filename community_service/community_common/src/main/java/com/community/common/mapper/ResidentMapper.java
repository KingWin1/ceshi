package com.community.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.dto.ResidentQueryDTO;
import com.community.common.vo.ResidentVO;
import com.community.common.pojo.Resident;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 居民Mapper接口
 */
@Mapper
public interface ResidentMapper extends BaseMapper<Resident> {

    /**
     * 分页+条件查询居民列表（多表联查）
     * @param page 分页对象
     * @param queryDTO 查询参数
     * @return 分页结果
     */
    IPage<ResidentVO> selectResidentPage(Page<ResidentVO> page, @Param("query") ResidentQueryDTO queryDTO);

    /**
     * 根据居民ID查询居民信息
     * @param residentId 居民ID
     * @return 居民对象
     */
    Resident selectResidentById(@Param("residentId") Integer residentId);

    /**
     * 根据居民ID修改居民信息
     * @param resident 居民信息对象
     */
    void updateResidentById(Resident resident);

    /**
     * 根据姓名或电话模糊查询居民信息
     * @param name 姓名
     * @param phone 电话
     * @return 居民信息集合
     */
    List<Resident> selectByNameOrPhone(@Param("name") String name, @Param("phone") String phone);
}
