package com.ocp.communication_service.controller;

import com.ocp.communication_service.dto.request.CreateConversationRequestDTO;
import com.ocp.communication_service.dto.response.ConversationResponseDTO;
import com.ocp.communication_service.service.ConversationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
            @Valid @RequestBody CreateConversationRequestDTO request) {
        ConversationResponseDTO response = conversationService.createConversation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConversationResponseDTO> getConversationById(@PathVariable UUID id) {
        return ResponseEntity.ok(conversationService.getConversationById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ConversationResponseDTO>> getConversationsByUser(
            @PathVariable UUID userId) {
        return ResponseEntity.ok(conversationService.getConversationsByUser(userId));
    }

    @PostMapping("/{conversationId}/members/{userId}")
    public ResponseEntity<Void> addMember(
            @PathVariable UUID conversationId, @PathVariable UUID userId) {
        conversationService.addMember(conversationId, userId);
        return ResponseEntity.noContent().build();
    }
}
