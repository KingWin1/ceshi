package com.community.backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.community.backend.dto.NoticeQueryDTO;
import com.community.common.pojo.Notice;

/**
 * 公告Service接口
 */
public interface NoticeService extends IService<Notice> {

    /**
     * 分页+条件查询公告列表
     * @param queryDTO 查询参数
     * @return 分页结果
     */
    IPage<Notice> selectNoticePage(NoticeQueryDTO queryDTO);

    /**
     * 根据ID查询公告信息
     * @param noticeId 公告ID
     * @return 公告对象
     */
    Notice getNoticeById(Integer noticeId);
}
