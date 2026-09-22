package com.community.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.backend.mapper.ReplyInfoMapper;
import com.community.backend.service.ReplyInfoService;
import com.community.common.pojo.ReplyInfo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 回复信息Service实现类
 */
@Service
public class ReplyInfoServiceImpl extends ServiceImpl<ReplyInfoMapper, ReplyInfo> implements ReplyInfoService {

    @Override
    public List<ReplyInfo> getReplyList(Integer complaintId, Integer replyType, Integer replyerId) {
        QueryWrapper<ReplyInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("complaint_id", complaintId)
                .eq("reply_type", replyType)
                .eq("replyer_id", replyerId)
                .orderByDesc("create_time");
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public void addReply(ReplyInfo replyInfo) {
        baseMapper.insert(replyInfo);
    }
}
