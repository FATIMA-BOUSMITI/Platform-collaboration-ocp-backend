package com.ocp.communication_service.service;

import com.ocp.communication_service.dto.request.CreateConversationRequestDTO;
import com.ocp.communication_service.entity.Conversation;
import com.ocp.communication_service.entity.ConversationMember;
import com.ocp.communication_service.exception.NotConversationMemberException;
import com.ocp.communication_service.mapper.ConversationMapper;
import com.ocp.communication_service.repository.ConversationMemberRepository;
import com.ocp.communication_service.repository.ConversationRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.access.AccessDeniedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConversationServiceAuthorizationTest {

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private ConversationMemberRepository conversationMemberRepository;

    @Mock
    private ConversationMapper conversationMapper;

    @InjectMocks
    private ConversationService conversationService;

    @Test
    void conversationCreatorMustMatchAuthenticatedUser() {
        CreateConversationRequestDTO request = new CreateConversationRequestDTO();
        request.setCreatorId(UUID.randomUUID());
        UUID authenticatedUserId = UUID.randomUUID();

        assertThrows(AccessDeniedException.class,
            () -> conversationService.createConversation(request, authenticatedUserId));
        verifyNoInteractions(conversationRepository, conversationMemberRepository, conversationMapper);
    }

    @Test
    void conversationDetailsAreLimitedToMembers() {
        UUID conversationId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(conversationRepository.findById(conversationId))
            .thenReturn(Optional.of(Conversation.builder().id(conversationId).build()));
        when(conversationMemberRepository
            .existsByConversationIdAndUserId(conversationId, userId)).thenReturn(false);

        assertThrows(NotConversationMemberException.class,
            () -> conversationService.getConversationById(conversationId, userId));
        verify(conversationMapper, never()).toResponse(
            org.mockito.ArgumentMatchers.any(Conversation.class));
    }

    @Test
    void onlyConversationAdminsCanAddMembers() {
        UUID conversationId = UUID.randomUUID();
        UUID actorId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        ConversationMember actor = ConversationMember.builder()
            .conversationId(conversationId)
            .userId(actorId)
            .role(ConversationMember.MemberRole.MEMBER)
            .build();
        when(conversationRepository.existsById(conversationId)).thenReturn(true);
        when(conversationMemberRepository
            .findByConversationIdAndUserId(conversationId, actorId))
            .thenReturn(Optional.of(actor));

        assertThrows(AccessDeniedException.class,
            () -> conversationService.addMember(conversationId, targetId, actorId));
        verify(conversationMemberRepository, never()).save(
            org.mockito.ArgumentMatchers.any(ConversationMember.class));
    }
}
