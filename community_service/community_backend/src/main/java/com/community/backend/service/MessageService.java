package com.community.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.community.backend.dto.MessageAddDTO;
import com.community.common.pojo.Message;

import java.util.List;

/**
 * 消息Service接口
 */
public interface MessageService extends IService<Message> {

    /**
     * 添加消息（催缴）
     * @param billId 账单ID
     * @param residentId 居民ID
     * @param title 消息标题
     * @param content 消息内容
     */
    void addMessage(Integer billId, Integer residentId, String title, String content);

    /**
     * 批量添加消息（批量催缴）
     * @param addDTOList 消息对象集合
     */
    void batchAddMessage(List<MessageAddDTO> addDTOList);
}
