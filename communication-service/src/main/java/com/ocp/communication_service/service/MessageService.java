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
    private final com.ocp.communication_service.realtime.RealtimeEvents events;
    private final AttachmentService attachments;
    private final NotificationService notifications;

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

        Message savedMessage = messageRepository.saveAndFlush(message);
        for(var member:conversationMemberRepository.findByConversationId(conversationId))if(!member.getUserId().equals(request.getSenderId()))notifications.publish(new NotificationService.Event(UUID.nameUUIDFromBytes(("message:"+savedMessage.getId()+":"+member.getUserId()).getBytes(java.nio.charset.StandardCharsets.UTF_8)),member.getUserId(),"MESSAGE","Nouveau message",request.getContent().substring(0,Math.min(180,request.getContent().length())),"/messages?conversation="+conversationId));
        var response=enrich(savedMessage);
        events.send(conversationMemberRepository.findByConversationId(conversationId).stream().map(com.ocp.communication_service.entity.ConversationMember::getUserId).toList(),"messages",java.util.Map.of("kind","message","conversationId",conversationId,"message",response));
        return response;
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
                .map(this::enrich)
                .collect(Collectors.toList());
    }
    private MessageResponseDTO enrich(Message message){var response=messageMapper.toResponse(message);response.setAttachments(attachments.list(message.getId()));response.setReadBy(conversationMemberRepository.findByConversationId(message.getConversationId()).stream().filter(m->!m.getUserId().equals(message.getSenderId())&&m.getLastReadAt()!=null&&message.getSentAt()!=null&&!m.getLastReadAt().isBefore(message.getSentAt())).map(com.ocp.communication_service.entity.ConversationMember::getUserId).toList());return response;}
    @Transactional public void markRead(UUID conversationId,UUID user){if(user==null)throw new AccessDeniedException("Authentification requise");var member=conversationMemberRepository.findByConversationIdAndUserId(conversationId,user).orElseThrow(()->new AccessDeniedException("Conversation non accessible"));member.setLastReadAt(java.time.LocalDateTime.now());conversationMemberRepository.save(member);events.send(conversationMemberRepository.findByConversationId(conversationId).stream().map(com.ocp.communication_service.entity.ConversationMember::getUserId).toList(),"messages",java.util.Map.of("kind","receipt","conversationId",conversationId,"userId",user));}
}
