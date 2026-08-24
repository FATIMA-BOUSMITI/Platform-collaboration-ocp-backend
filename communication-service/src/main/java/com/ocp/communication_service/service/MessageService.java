package com.ocp.communication_service.service;

import com.ocp.communication_service.dto.request.SendMessageRequestDTO;
import com.ocp.communication_service.dto.response.MessageResponseDTO;
import com.ocp.communication_service.entity.Message;
import com.ocp.communication_service.exception.ConversationNotFoundException;
import com.ocp.communication_service.exception.NotConversationMemberException;
import com.ocp.communication_service.mapper.MessageMapper;
import com.ocp.communication_service.repository.ConversationMemberRepository;
import com.ocp.communication_service.repository.ConversationRepository;
import com.ocp.communication_service.repository.MessageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final ConversationMemberRepository conversationMemberRepository;
    private final MessageMapper messageMapper;

    @Transactional
    public MessageResponseDTO sendMessage(UUID conversationId, SendMessageRequestDTO request) {
        if (!conversationRepository.existsById(conversationId)) {
            throw new ConversationNotFoundException(conversationId.toString());
        }

        boolean isMember = conversationMemberRepository
                .existsByConversationIdAndUserId(conversationId, request.getSenderId());
        if (!isMember) {
            throw new NotConversationMemberException(
                    request.getSenderId().toString(), conversationId.toString());
        }

        Message message = Message.builder()
                .conversationId(conversationId)
                .senderId(request.getSenderId())
                .content(request.getContent())
                .build();

        Message savedMessage = messageRepository.save(message);
        return messageMapper.toResponse(savedMessage);
    }

    @Transactional(readOnly = true)
    public List<MessageResponseDTO> getMessagesByConversation(UUID conversationId) {
        if (!conversationRepository.existsById(conversationId)) {
            throw new ConversationNotFoundException(conversationId.toString());
        }

        return messageRepository.findByConversationIdOrderBySentAtAsc(conversationId).stream()
                .map(messageMapper::toResponse)
                .collect(Collectors.toList());
    }
}
