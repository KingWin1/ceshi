package com.community.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.common.pojo.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 消息Mapper接口
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    /**
     * 根据账单ID统计催缴次数
     * @param billId 账单ID
     * @return 催缴次数
     */
    Integer countByBillId(@Param("billId") Integer billId);

    /**
     * 根据账单ID查询最后催缴时间
     * @param billId 账单ID
     * @return 最后催缴时间
     */
    java.util.Date selectLastRemindTime(@Param("billId") Integer billId);
}
