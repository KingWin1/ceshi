package com.community.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.backend.dto.NoticeQueryDTO;
import com.community.backend.mapper.NoticeMapper;
import com.community.backend.service.NoticeService;
import com.community.common.pojo.Notice;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 公告Service实现类
 */
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    @Override
    public IPage<Notice> selectNoticePage(NoticeQueryDTO queryDTO) {
        Page<Notice> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        // 标题模糊查询
        if (StringUtils.hasText(queryDTO.getTitle())) {
            wrapper.like(Notice::getTitle, queryDTO.getTitle());
        }
        // 状态精确查询
        if (queryDTO.getStatus() != null) {
            wrapper.eq(Notice::getStatus, queryDTO.getStatus());
        }
        // 是否置顶精确查询
        if (queryDTO.getIsTop() != null) {
            wrapper.eq(Notice::getIsTop, queryDTO.getIsTop());
        }
        // 置顶优先，再按创建时间倒序
        wrapper.orderByDesc(Notice::getIsTop).orderByDesc(Notice::getCreateTime);

        return page(page, wrapper);
    }

    @Override
    public Notice getNoticeById(Integer noticeId) {
        return getById(noticeId);
    }
}
