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
import org.springframework.security.access.AccessDeniedException;
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
    public MessageResponseDTO sendMessage(
            UUID conversationId, SendMessageRequestDTO request, UUID authenticatedUserId) {
        if (!conversationRepository.existsById(conversationId)) {
            throw new ConversationNotFoundException(conversationId.toString());
        }

        if (authenticatedUserId != null
                && !authenticatedUserId.equals(request.getSenderId())) {
            throw new AccessDeniedException("The sender must match the authenticated user");
        }

        if (authenticatedUserId == null) {
            if (!conversationMemberRepository.existsByConversationIdAndUserId(
                    conversationId, request.getSenderId())) {
                throw new NotConversationMemberException(
                        request.getSenderId().toString(), conversationId.toString());
            }
        } else if (!conversationMemberRepository.existsByConversationIdAndUserId(
                conversationId, authenticatedUserId)) {
            throw new NotConversationMemberException(
                    authenticatedUserId.toString(), conversationId.toString());
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
    public List<MessageResponseDTO> getMessagesByConversation(
            UUID conversationId, UUID authenticatedUserId) {
        if (!conversationRepository.existsById(conversationId)) {
            throw new ConversationNotFoundException(conversationId.toString());
        }
        if (authenticatedUserId != null && !conversationMemberRepository
                .existsByConversationIdAndUserId(conversationId, authenticatedUserId)) {
            throw new NotConversationMemberException(
                    authenticatedUserId.toString(), conversationId.toString());
        }

        return messageRepository.findByConversationIdOrderBySentAtAsc(conversationId).stream()
                .map(messageMapper::toResponse)
                .collect(Collectors.toList());
    }
}
