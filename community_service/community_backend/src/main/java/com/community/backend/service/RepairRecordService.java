package com.community.backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.community.backend.dto.RepairRecordQueryDTO;
import com.community.backend.vo.RepairRecordVO;
import com.community.common.pojo.RepairRecord;

import java.util.List;

/**
 * 报修记录Service接口
 */
public interface RepairRecordService extends IService<RepairRecord> {

    /**
     * 分页+条件查询报修记录
     * @param queryDTO 查询参数
     * @return 分页结果
     */
    IPage<RepairRecordVO> selectRepairRecordPage(RepairRecordQueryDTO queryDTO);

    /**
     * 根据报修ID查询报修记录详情
     * @param repairId 报修ID
     * @return 报修记录详情
     */
    RepairRecordVO getRepairRecordDetail(Integer repairId);

    /**
     * 派单
     * @param repairId 报修ID
     * @param employeeId 员工ID
     */
    void dispatch(Integer repairId, Integer employeeId);

    /**
     * 完工
     * @param repairId 报修ID
     * @param completeRemark 完工备注
     * @param completeImageUrl 完工图片地址
     */
    void complete(Integer repairId, String completeRemark, String completeImageUrl);

    /**
     * 添加报修记录
     * @param repairRecord 报修记录对象
     */
    void addRepairRecord(RepairRecord repairRecord);
}
