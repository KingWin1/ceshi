package com.community.backend.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.backend.dto.RepairRecordQueryDTO;
import com.community.backend.mapper.RepairRecordMapper;
import com.community.backend.service.RepairRecordService;
import com.community.backend.vo.RepairRecordVO;
import com.community.common.pojo.RepairRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 报修记录Service实现类
 */
@Service
public class RepairRecordServiceImpl extends ServiceImpl<RepairRecordMapper, RepairRecord> implements RepairRecordService {

    @Override
    public IPage<RepairRecordVO> selectRepairRecordPage(RepairRecordQueryDTO queryDTO) {
        Page<RepairRecordVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        return baseMapper.selectRepairRecordPage(page, queryDTO);
    }

    @Override
    public RepairRecordVO getRepairRecordDetail(Integer repairId) {
        return baseMapper.selectRepairRecordDetail(repairId);
    }

    @Override
    @Transactional
    public void dispatch(Integer repairId, Integer employeeId) {
        RepairRecord record = baseMapper.selectById(repairId);
        if (record == null) {
            throw new RuntimeException("报修记录不存在");
        }
        record.setEmployeeId(employeeId);
        record.setStatus(2); // 已派单
        record.setDispatchTime(new Date());
        baseMapper.updateById(record);
    }

    @Override
    @Transactional
    public void complete(Integer repairId, String completeRemark, String completeImageUrl) {
        RepairRecord record = baseMapper.selectById(repairId);
        if (record == null) {
            throw new RuntimeException("报修记录不存在");
        }
        record.setCompleteRemark(completeRemark);
        record.setCompleteImageUrl(completeImageUrl);
        record.setStatus(4); // 已完成
        record.setCompleteTime(new Date());
        baseMapper.updateById(record);
    }

    @Override
    public void addRepairRecord(RepairRecord repairRecord) {
        // 报修单号由后端生成：WX + 毫秒时间戳
        if (repairRecord.getRepairNo() == null || repairRecord.getRepairNo().trim().isEmpty()) {
            repairRecord.setRepairNo("WX" + System.currentTimeMillis());
        }
        // 新增默认待派单
        if (repairRecord.getStatus() == null) {
            repairRecord.setStatus(1);
        }
        baseMapper.insert(repairRecord);
    }
}
