package com.community.backend.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.backend.dto.ComplaintRecordQueryDTO;
import com.community.backend.mapper.ComplaintRecordMapper;
import com.community.backend.service.ComplaintRecordService;
import com.community.common.pojo.ComplaintRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 投诉记录Service实现类
 */
@Service
public class ComplaintRecordServiceImpl extends ServiceImpl<ComplaintRecordMapper, ComplaintRecord> implements ComplaintRecordService {

    @Override
    public IPage<ComplaintRecord> selectComplaintRecordPage(ComplaintRecordQueryDTO queryDTO) {
        Page<ComplaintRecord> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        return baseMapper.selectComplaintRecordPage(page, queryDTO);
    }

    @Override
    @Transactional
    public void replyComplaint(Integer complaintId) {
        ComplaintRecord record = baseMapper.selectById(complaintId);
        if (record == null) {
            throw new RuntimeException("投诉记录不存在");
        }
        record.setStatus(3); // 已回复
        baseMapper.updateById(record);
    }

    @Override
    @Transactional
    public void updateComplaintStatus(Integer complaintId, Integer status) {
        ComplaintRecord record = baseMapper.selectById(complaintId);
        if (record == null) {
            throw new RuntimeException("投诉记录不存在");
        }
        record.setStatus(status);
        baseMapper.updateById(record);
    }

    @Override
    @Transactional
    public void addComplaint(ComplaintRecord complaintRecord) {
        // 自动生成投诉编号：CS + 时间戳（与报修单号 WX + 时间戳规则一致）
        complaintRecord.setComplaintNo("CS" + System.currentTimeMillis());
        // 新提交的投诉默认状态为待处理
        complaintRecord.setStatus(1);
        baseMapper.insert(complaintRecord);
    }
}
