package com.community.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.backend.dto.MessageAddDTO;
import com.community.backend.mapper.MessageMapper;
import com.community.backend.service.MessageService;
import com.community.common.pojo.Message;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 消息Service实现类
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Override
    public void addMessage(Integer billId, Integer residentId, String title, String content) {
        Message message = new Message();
        message.setBillId(billId);
        message.setResidentId(residentId);
        message.setTitle(title);
        message.setContent(content);
        baseMapper.insert(message);
    }

    @Override
    @Transactional
    public void batchAddMessage(List<MessageAddDTO> addDTOList) {
        if (addDTOList == null || addDTOList.isEmpty()) {
            return;
        }
        for (MessageAddDTO addDTO : addDTOList) {
            addMessage(addDTO.getBillId(), addDTO.getResidentId(), addDTO.getTitle(), addDTO.getContent());
        }
    }
}
