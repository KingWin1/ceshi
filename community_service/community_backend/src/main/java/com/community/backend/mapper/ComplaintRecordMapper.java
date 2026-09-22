package com.community.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.backend.dto.ComplaintRecordQueryDTO;
import com.community.common.pojo.ComplaintRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 投诉记录Mapper接口
 */
@Mapper
public interface ComplaintRecordMapper extends BaseMapper<ComplaintRecord> {

    /**
     * 分页+条件查询投诉记录
     * @param page 分页对象
     * @param queryDTO 查询参数
     * @return 分页结果
     */
    IPage<ComplaintRecord> selectComplaintRecordPage(Page<ComplaintRecord> page,
                                                      @Param("query") ComplaintRecordQueryDTO queryDTO);
}
