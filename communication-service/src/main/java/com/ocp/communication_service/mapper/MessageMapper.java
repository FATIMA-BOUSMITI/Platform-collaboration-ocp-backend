package com.ocp.communication_service.mapper;

import com.ocp.communication_service.dto.response.MessageResponseDTO;
import com.ocp.communication_service.entity.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    public MessageResponseDTO toResponse(Message message) {
        return new MessageResponseDTO(
                message.getId(),
                message.getConversationId(),
                message.getSenderId(),
                message.getContent(),
                message.getSentAt(),
                message.getStatus().name()
        );
    }
}
