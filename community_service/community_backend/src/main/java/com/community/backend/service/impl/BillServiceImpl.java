package com.community.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.backend.dto.BillAddDTO;
import com.community.backend.dto.BillImportRowDTO;
import com.community.backend.dto.BillQueryDTO;
import com.community.backend.mapper.BillMapper;
import com.community.backend.mapper.BuildingMapper;
import com.community.backend.mapper.FeeTypeMapper;
import com.community.common.mapper.HouseMapper;
import com.community.backend.service.BillService;
import com.community.backend.vo.BillVO;
import com.community.common.pojo.Bill;
import com.community.common.pojo.Building;
import com.community.common.pojo.FeeType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 账单Service实现类
 */
@Service
public class BillServiceImpl extends ServiceImpl<BillMapper, Bill> implements BillService {

    @Autowired
    private BuildingMapper buildingMapper;

    @Autowired
    private HouseMapper houseMapper;

    @Autowired
    private FeeTypeMapper feeTypeMapper;

    @Override
    public IPage<BillVO> selectBillPage(BillQueryDTO queryDTO) {
        Page<BillVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        return baseMapper.selectBillPage(page, queryDTO);
    }

    @Override
    public BillVO getBillDetail(Integer billId) {
        return baseMapper.selectBillDetail(billId);
    }

    @Override
    public List<BillVO> getBillList(BillQueryDTO queryDTO) {
        return baseMapper.selectBillList(queryDTO);
    }

    @Override
    @Transactional
    public void addBill(BillAddDTO addDTO) {
        Bill bill = new Bill();
        BeanUtils.copyProperties(addDTO, bill);
        // 如果未设置已付金额，默认为0
        if (bill.getPaidAmount() == null) {
            bill.setPaidAmount(BigDecimal.ZERO);
        }
        // 如果未设置优惠金额，默认为0
        if (bill.getDiscountAmount() == null) {
            bill.setDiscountAmount(BigDecimal.ZERO);
        }
        // 如果未设置状态，默认为未缴
        if (bill.getStatus() == null) {
            bill.setStatus(2);
        }
        baseMapper.insert(bill);
    }

    @Override
    @Transactional
    public void batchAddBill(List<BillAddDTO> addDTOList) {
        for (BillAddDTO addDTO : addDTOList) {
            addBill(addDTO);
        }
    }

    @Override
    @Transactional
    public void payBill(Integer billId, Integer payMethod, String paySerialNo) {
        Bill bill = baseMapper.selectById(billId);
        if (bill == null) {
            throw new RuntimeException("账单不存在");
        }
        bill.setPayMethod(payMethod);
        bill.setPaySerialNo(paySerialNo);
        bill.setPayTime(new Date());
        bill.setStatus(1); // 已缴
        bill.setPaidAmount(bill.getPayableAmount());
        baseMapper.updateById(bill);
    }

    @Override
    @Transactional
    public String importBills(List<BillImportRowDTO> rowList, boolean overwrite) {
        if (rowList == null || rowList.isEmpty()) {
            throw new RuntimeException("导入文件中没有有效数据");
        }
        // 楼栋名称 -> 楼栋ID
        Map<String, Integer> buildingMap = new HashMap<>();
        for (Building building : buildingMapper.selectList(null)) {
            buildingMap.put(building.getBuildingName(), building.getBuildingId());
        }
        // 费用类型名称 -> 费用类型
        Map<String, FeeType> feeTypeMap = new HashMap<>();
        for (FeeType feeType : feeTypeMapper.selectList(null)) {
            feeTypeMap.put(feeType.getFeeTypeName(), feeType);
        }

        List<BillAddDTO> addList = new ArrayList<>();
        List<String> failMsgs = new ArrayList<>();
        int rowNum = 1;
        for (BillImportRowDTO row : rowList) {
            rowNum++;
            try {
                BillAddDTO dto = convertImportRow(row, rowNum, buildingMap, feeTypeMap);
                if (overwrite) {
                    removeDuplicateBill(dto);
                }
                addList.add(dto);
            } catch (RuntimeException e) {
                failMsgs.add(e.getMessage());
            }
        }
        if (!addList.isEmpty()) {
            batchAddBill(addList);
        }
        StringBuilder msg = new StringBuilder("导入完成，成功" + addList.size() + "条，失败" + failMsgs.size() + "条");
        if (!failMsgs.isEmpty()) {
            msg.append("。失败原因：");
            int limit = Math.min(failMsgs.size(), 5);
            for (int i = 0; i < limit; i++) {
                if (i > 0) {
                    msg.append("；");
                }
                msg.append(failMsgs.get(i));
            }
            if (failMsgs.size() > limit) {
                msg.append(" 等");
            }
        }
        return msg.toString();
    }

    /**
     * 导入行数据转换为添加参数：匹配房屋ID与费用类型ID，校验必填项
     */
    private BillAddDTO convertImportRow(BillImportRowDTO row, int rowNum,
                                        Map<String, Integer> buildingMap,
                                        Map<String, FeeType> feeTypeMap) {
        if (!StringUtils.hasText(row.getBuildingName())) {
            throw new RuntimeException("第" + rowNum + "行楼栋名称为空");
        }
        Integer buildingId = buildingMap.get(row.getBuildingName().trim());
        if (buildingId == null) {
            throw new RuntimeException("第" + rowNum + "行楼栋[" + row.getBuildingName() + "]不存在");
        }
        // 单元号归一化：兼容"1单元"写法
        String unitNo = normalizeUnitNo(row.getUnitNo());
        if (!StringUtils.hasText(row.getHouseNumber())) {
            throw new RuntimeException("第" + rowNum + "行房间号为空");
        }
        List<Integer> houseIds = houseMapper.selectHouseIdsByBuildingIdAndUnitNoAndHouseNumber(
                buildingId, unitNo, row.getHouseNumber().trim());
        if (houseIds == null || houseIds.isEmpty()) {
            throw new RuntimeException("第" + rowNum + "行房间[" + row.getBuildingName() + "-"
                    + unitNo + "单元-" + row.getHouseNumber() + "]不存在");
        }
        if (!StringUtils.hasText(row.getFeeTypeName())) {
            throw new RuntimeException("第" + rowNum + "行费用类型名称为空");
        }
        FeeType feeType = feeTypeMap.get(row.getFeeTypeName().trim());
        if (feeType == null) {
            throw new RuntimeException("第" + rowNum + "行费用类型[" + row.getFeeTypeName() + "]不存在");
        }
        if (row.getBillAmount() == null) {
            throw new RuntimeException("第" + rowNum + "行账单金额为空");
        }
        if (row.getStartDate() == null || row.getEndDate() == null) {
            throw new RuntimeException("第" + rowNum + "行计费开始/结束日期为空或格式错误（应为yyyy-MM-dd）");
        }

        BillAddDTO dto = new BillAddDTO();
        dto.setBillNo(StringUtils.hasText(row.getBillNo())
                ? row.getBillNo().trim() : "B" + System.currentTimeMillis() + rowNum);
        dto.setHouseId(houseIds.get(0));
        dto.setFeeTypeId(feeType.getFeeTypeId());
        dto.setBillAmount(row.getBillAmount());
        dto.setDiscountAmount(row.getDiscountAmount() != null ? row.getDiscountAmount() : BigDecimal.ZERO);
        dto.setPayableAmount(row.getBillAmount().subtract(dto.getDiscountAmount()));
        dto.setBillingPeriod(StringUtils.hasText(row.getBillingPeriod()) ? row.getBillingPeriod().trim() : null);
        dto.setBillingPeriodValue(row.getBillingPeriodValue());
        dto.setStartDate(row.getStartDate());
        dto.setEndDate(row.getEndDate());
        dto.setStatus(2);
        dto.setPaidAmount(BigDecimal.ZERO);
        return dto;
    }

    /**
     * 覆盖模式：删除同房屋+同费用类型+同计费开始日期的重复账单
     */
    private void removeDuplicateBill(BillAddDTO dto) {
        LambdaQueryWrapper<Bill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Bill::getHouseId, dto.getHouseId())
                .eq(Bill::getFeeTypeId, dto.getFeeTypeId())
                .eq(Bill::getStartDate, dto.getStartDate());
        remove(wrapper);
    }

    /**
     * 单元号归一化：去掉"单元"后缀，纯数字保持原样
     */
    private String normalizeUnitNo(String unitNo) {
        if (unitNo == null) {
            return "";
        }
        String value = unitNo.trim();
        if (value.endsWith("单元")) {
            value = value.substring(0, value.length() - 2);
        }
        return value;
    }
}
