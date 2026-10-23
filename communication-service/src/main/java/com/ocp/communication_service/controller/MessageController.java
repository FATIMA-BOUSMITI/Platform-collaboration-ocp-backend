package com.ocp.communication_service.controller;

import com.ocp.communication_service.dto.request.SendMessageRequestDTO;
import com.ocp.communication_service.dto.response.MessageResponseDTO;
import com.ocp.communication_service.security.AuthenticatedUser;
import com.ocp.communication_service.service.MessageService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/conversations/{conversationId}/messages")
@AllArgsConstructor
public class MessageController {

    private final MessageService messageService;
    @PatchMapping("/read") @ResponseStatus(HttpStatus.NO_CONTENT) public void read(@PathVariable UUID conversationId,@AuthenticationPrincipal AuthenticatedUser user){messageService.markRead(conversationId,user==null?null:user.userId());}

    @PostMapping
    public ResponseEntity<MessageResponseDTO> sendMessage(
            @PathVariable UUID conversationId,
            @Valid @RequestBody SendMessageRequestDTO request,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        MessageResponseDTO response = messageService.sendMessage(
                conversationId, request,
                authenticatedUser == null ? null : authenticatedUser.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<MessageResponseDTO>> getMessages(
            @PathVariable UUID conversationId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return ResponseEntity.ok(
                messageService.getMessagesByConversation(
                        conversationId,
                        authenticatedUser == null ? null : authenticatedUser.userId()));
    }
}
