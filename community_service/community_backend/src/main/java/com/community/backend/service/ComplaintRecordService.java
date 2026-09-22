package com.community.backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.community.backend.dto.ComplaintRecordQueryDTO;
import com.community.common.pojo.ComplaintRecord;

/**
 * 投诉记录Service接口
 */
public interface ComplaintRecordService extends IService<ComplaintRecord> {

    /**
     * 分页+条件查询投诉记录
     * @param queryDTO 查询参数
     * @return 分页结果
     */
    IPage<ComplaintRecord> selectComplaintRecordPage(ComplaintRecordQueryDTO queryDTO);

    /**
     * 根据投诉记录ID修改投诉记录状态为已回复
     * @param complaintId 投诉记录ID
     */
    void replyComplaint(Integer complaintId);

    /**
     * 根据投诉ID修改投诉状态
     * @param complaintId 投诉记录ID
     * @param status 状态
     */
    void updateComplaintStatus(Integer complaintId, Integer status);

    /**
     * 添加投诉记录（自动生成投诉编号，默认状态待处理）
     * @param complaintRecord 投诉记录对象
     */
    void addComplaint(ComplaintRecord complaintRecord);
}
