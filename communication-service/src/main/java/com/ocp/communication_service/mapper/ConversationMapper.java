package com.ocp.communication_service.mapper;

import com.ocp.communication_service.dto.response.ConversationResponseDTO;
import com.ocp.communication_service.entity.Conversation;
import org.springframework.stereotype.Component;

@Component
public class ConversationMapper {

    public ConversationResponseDTO toResponse(Conversation conversation) {
        return new ConversationResponseDTO(
                conversation.getId(),
                conversation.getType().name(),
                conversation.getName(),
                conversation.getCreatedAt()
        );
    }
}