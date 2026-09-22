package com.community.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.community.common.pojo.ReplyInfo;

import java.util.List;

/**
 * 回复信息Service接口
 */
public interface ReplyInfoService extends IService<ReplyInfo> {

    /**
     * 根据投诉记录ID、回复人类型、回复人ID查询回复信息集合
     * @param complaintId 投诉记录ID
     * @param replyType 回复人类型：1-用户 2-管理员
     * @param replyerId 回复人ID
     * @return 回复信息集合
     */
    List<ReplyInfo> getReplyList(Integer complaintId, Integer replyType, Integer replyerId);

    /**
     * 添加回复信息
     * @param replyInfo 回复信息对象
     */
    void addReply(ReplyInfo replyInfo);
}
