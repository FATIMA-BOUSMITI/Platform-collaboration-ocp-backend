package com.ocp.communication_service.service;

import com.ocp.communication_service.dto.request.CreateConversationRequestDTO;
import com.ocp.communication_service.dto.response.ConversationResponseDTO;
import com.ocp.communication_service.entity.Conversation;
import com.ocp.communication_service.entity.ConversationMember;
import com.ocp.communication_service.exception.ConversationNotFoundException;
import com.ocp.communication_service.mapper.ConversationMapper;
import com.ocp.communication_service.repository.ConversationMemberRepository;
import com.ocp.communication_service.repository.ConversationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final ConversationMemberRepository conversationMemberRepository;
    private final ConversationMapper conversationMapper;

    @Transactional
    public ConversationResponseDTO createConversation(CreateConversationRequestDTO request) {
        Conversation conversation = Conversation.builder()
                .type(Conversation.ConversationType.valueOf(request.getType()))
                .name(request.getName())
                .build();

        Conversation savedConversation = conversationRepository.save(conversation);

        ConversationMember creatorMember = ConversationMember.builder()
                .conversationId(savedConversation.getId())
                .userId(request.getCreatorId())
                .role(ConversationMember.MemberRole.ADMIN)
                .build();
        conversationMemberRepository.save(creatorMember);

        for (UUID memberId : request.getMemberIds()) {
            if (!memberId.equals(request.getCreatorId())) {
                ConversationMember member = ConversationMember.builder()
                        .conversationId(savedConversation.getId())
                        .userId(memberId)
                        .role(ConversationMember.MemberRole.MEMBER)
                        .build();
                conversationMemberRepository.save(member);
            }
        }

        return conversationMapper.toResponse(savedConversation);
    }

    @Transactional(readOnly = true)
    public ConversationResponseDTO getConversationById(UUID id) {
        Conversation conversation = conversationRepository.findById(id)
                .orElseThrow(() -> new ConversationNotFoundException(id.toString()));
        return conversationMapper.toResponse(conversation);
    }

    @Transactional(readOnly = true)
    public List<ConversationResponseDTO> getConversationsByUser(UUID userId) {
        List<UUID> conversationIds = conversationMemberRepository.findByUserId(userId).stream()
                .map(ConversationMember::getConversationId)
                .collect(Collectors.toList());

        return conversationRepository.findAllById(conversationIds).stream()
                .map(conversationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void addMember(UUID conversationId, UUID userId) {
        if (!conversationRepository.existsById(conversationId)) {
            throw new ConversationNotFoundException(conversationId.toString());
        }

        if (conversationMemberRepository.existsByConversationIdAndUserId(conversationId, userId)) {
            return; // déjà membre, rien à faire — idempotent
        }

        ConversationMember member = ConversationMember.builder()
                .conversationId(conversationId)
                .userId(userId)
                .role(ConversationMember.MemberRole.MEMBER)
                .build();
        conversationMemberRepository.save(member);
    }
}