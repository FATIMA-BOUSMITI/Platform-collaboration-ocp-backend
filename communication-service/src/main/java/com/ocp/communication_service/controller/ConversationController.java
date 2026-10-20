package com.ocp.communication_service.controller;

import com.ocp.communication_service.dto.request.CreateConversationRequestDTO;
import com.ocp.communication_service.dto.response.ConversationResponseDTO;
import com.ocp.communication_service.security.AuthenticatedUser;
import com.ocp.communication_service.service.ConversationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/conversations")
@AllArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;

    @PostMapping
    public ResponseEntity<ConversationResponseDTO> createConversation(
            @Valid @RequestBody CreateConversationRequestDTO request,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        ConversationResponseDTO response =
                conversationService.createConversation(request,
                    authenticatedUser == null ? null : authenticatedUser.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConversationResponseDTO> getConversationById(
            @PathVariable UUID id,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return ResponseEntity.ok(
                conversationService.getConversationById(id,
                    authenticatedUser == null ? null : authenticatedUser.userId()));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ConversationResponseDTO>> getConversationsByUser(
            @PathVariable UUID userId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        if (authenticatedUser != null && !userId.equals(authenticatedUser.userId())) {
            throw new AccessDeniedException("Users can only list their own conversations");
        }
        return ResponseEntity.ok(conversationService.getConversationsByUser(userId));
    }

    @PostMapping("/{conversationId}/members/{userId}")
    public ResponseEntity<Void> addMember(
            @PathVariable UUID conversationId,
            @PathVariable UUID userId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        conversationService.addMember(conversationId, userId,
            authenticatedUser == null ? null : authenticatedUser.userId());
        return ResponseEntity.noContent().build();
    }
}
