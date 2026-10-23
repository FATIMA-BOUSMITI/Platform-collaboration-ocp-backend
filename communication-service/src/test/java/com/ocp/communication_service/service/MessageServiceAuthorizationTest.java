package com.ocp.communication_service.service;

import com.ocp.communication_service.dto.request.SendMessageRequestDTO;
import com.ocp.communication_service.mapper.MessageMapper;
import com.ocp.communication_service.repository.ConversationMemberRepository;
import com.ocp.communication_service.repository.ConversationRepository;
import com.ocp.communication_service.repository.MessageRepository;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceAuthorizationTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private ConversationMemberRepository conversationMemberRepository;

    @Mock
    private MessageMapper messageMapper;

    @InjectMocks
    private MessageService messageService;

    @Test
    void senderMustMatchAuthenticatedUser() {
        UUID conversationId = UUID.randomUUID();
        SendMessageRequestDTO request = new SendMessageRequestDTO();
        request.setSenderId(UUID.randomUUID());
        request.setContent("test message");
        when(conversationRepository.existsById(conversationId)).thenReturn(true);

        assertThrows(AccessDeniedException.class,
            () -> messageService.sendMessage(conversationId, request, UUID.randomUUID()));
        verify(messageRepository, never()).save(
            org.mockito.ArgumentMatchers.any());
    }
}
