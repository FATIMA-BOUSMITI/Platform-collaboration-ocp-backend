package com.ocp.communication_service.service;

import com.ocp.communication_service.dto.request.CreateConversationRequestDTO;
import com.ocp.communication_service.dto.response.ConversationResponseDTO;
import com.ocp.communication_service.entity.Conversation;
import com.ocp.communication_service.entity.ConversationMember;
import com.ocp.communication_service.exception.ConversationNotFoundException;
import com.ocp.communication_service.exception.NotConversationMemberException;
import com.ocp.communication_service.mapper.ConversationMapper;
import com.ocp.communication_service.repository.ConversationMemberRepository;
import com.ocp.communication_service.repository.ConversationRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
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
    public ConversationResponseDTO createConversation(
            CreateConversationRequestDTO request, UUID authenticatedUserId) {
        if (authenticatedUserId != null
                && !authenticatedUserId.equals(request.getCreatorId())) {
            throw new AccessDeniedException("The creator must match the authenticated user");
        }

        var ids=new java.util.HashSet<>(request.getMemberIds());ids.add(request.getCreatorId());
        if(ids.contains(null)||ids.size()>100)throw new IllegalArgumentException("Participants invalides (100 maximum)");
        if("PRIVATE".equals(request.getType())&&ids.size()!=2)throw new IllegalArgumentException("Une conversation privée nécessite deux participants");
        if(!"PRIVATE".equals(request.getType())&&(request.getName()==null||request.getName().isBlank()))throw new IllegalArgumentException("Le nom du groupe est obligatoire");
        if("PROJECT".equals(request.getType())&&request.getProjectId()==null)throw new IllegalArgumentException("Projet requis");
        Conversation conversation = Conversation.builder()
                .type(Conversation.ConversationType.valueOf(request.getType()))
                .name(request.getName())
                .projectId(request.getProjectId())
                .build();

        Conversation savedConversation = conversationRepository.save(conversation);

        ConversationMember creatorMember = ConversationMember.builder()
                .conversationId(savedConversation.getId())
                .userId(request.getCreatorId())
                .role(ConversationMember.MemberRole.ADMIN)
                .build();
        conversationMemberRepository.save(creatorMember);

        for (UUID memberId : ids) {
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
    public ConversationResponseDTO getConversationById(UUID id, UUID authenticatedUserId) {
        Conversation conversation = conversationRepository.findById(id)
                .orElseThrow(() -> new ConversationNotFoundException(id.toString()));
        if (authenticatedUserId != null) {
            requireMembership(id, authenticatedUserId);
        }
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
    public void addMember(UUID conversationId, UUID userId, UUID authenticatedUserId) {
        if (!conversationRepository.existsById(conversationId)) {
            throw new ConversationNotFoundException(conversationId.toString());
        }

        if (authenticatedUserId != null) {
            ConversationMember actor = conversationMemberRepository
                    .findByConversationIdAndUserId(conversationId, authenticatedUserId)
                    .orElseThrow(() -> new NotConversationMemberException(
                            authenticatedUserId.toString(), conversationId.toString()));
            if (actor.getRole() != ConversationMember.MemberRole.ADMIN) {
                throw new AccessDeniedException("Only conversation admins can add members");
            }
        }

        if (conversationMemberRepository.existsByConversationIdAndUserId(conversationId, userId)) {
            return;
        }

        ConversationMember member = ConversationMember.builder()
                .conversationId(conversationId)
                .userId(userId)
                .role(ConversationMember.MemberRole.MEMBER)
                .build();
        conversationMemberRepository.save(member);
    }

    private void requireMembership(UUID conversationId, UUID userId) {
        if (!conversationMemberRepository.existsByConversationIdAndUserId(conversationId, userId)) {
            throw new NotConversationMemberException(userId.toString(), conversationId.toString());
        }
    }
}