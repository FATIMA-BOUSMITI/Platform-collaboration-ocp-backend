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
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final ConversationMemberRepository conversationMemberRepository;
    private final ConversationMapper conversationMapper;
    @org.springframework.beans.factory.annotation.Autowired private com.ocp.communication_service.realtime.ProjectConversationAccess projectAccess;

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
        if("PROJECT".equals(request.getType())&&authenticatedUserId!=null) projectAccess.requireRead(request.getProjectId());
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

        return view(savedConversation);
    }

    @Transactional(readOnly = true)
    public ConversationResponseDTO getConversationById(UUID id, UUID authenticatedUserId) {
        Conversation conversation = conversationRepository.findById(id)
                .orElseThrow(() -> new ConversationNotFoundException(id.toString()));
        if (authenticatedUserId != null) {
            requireMembership(id, authenticatedUserId);
        }
        return view(conversation);
    }

    @Transactional(readOnly = true)
    public List<ConversationResponseDTO> getConversationsByUser(UUID userId) {
        List<UUID> conversationIds = conversationMemberRepository.findByUserId(userId).stream()
                .map(ConversationMember::getConversationId)
                .collect(Collectors.toList());

        return conversationRepository.findAllById(conversationIds).stream()
                .map(this::view)
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

        var conversation=conversationRepository.findById(conversationId).orElseThrow();
        if(conversation.getType()==Conversation.ConversationType.PRIVATE) throw new IllegalArgumentException("Une conversation privée conserve deux participants");
        if(conversationMemberRepository.findByConversationId(conversationId).size()>=100) throw new IllegalArgumentException("100 participants maximum");
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

    private ConversationResponseDTO view(Conversation conversation) {
        var response=conversationMapper.toResponse(conversation);
        response.setProjectId(conversation.getProjectId());
        var members=conversationMemberRepository.findByConversationId(conversation.getId());
        response.setMemberIds(members.stream().map(ConversationMember::getUserId).toList());
        response.setAdminIds(members.stream().filter(m->m.getRole()==ConversationMember.MemberRole.ADMIN).map(ConversationMember::getUserId).toList());
        return response;
    }

    private void requireMembership(UUID conversationId, UUID userId) {
        if (!conversationMemberRepository.existsByConversationIdAndUserId(conversationId, userId)) {
            throw new NotConversationMemberException(userId.toString(), conversationId.toString());
        }
    }
}