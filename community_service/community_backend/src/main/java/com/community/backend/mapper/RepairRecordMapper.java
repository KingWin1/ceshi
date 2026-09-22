package com.community.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.backend.dto.RepairRecordQueryDTO;
import com.community.backend.vo.RepairRecordVO;
import com.community.common.pojo.RepairRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 报修记录Mapper接口
 */
@Mapper
public interface RepairRecordMapper extends BaseMapper<RepairRecord> {

    /**
     * 分页+条件查询报修记录（3表联查：报修记录表-报修类型表-员工表）
     * @param page 分页对象
     * @param queryDTO 查询参数
     * @return 分页结果
     */
    IPage<RepairRecordVO> selectRepairRecordPage(Page<RepairRecordVO> page,
                                                  @Param("query") RepairRecordQueryDTO queryDTO);

    /**
     * 根据报修ID查询报修记录详情（3表联查）
     * @param repairId 报修ID
     * @return 报修记录详情
     */
    RepairRecordVO selectRepairRecordDetail(@Param("repairId") Integer repairId);
}
