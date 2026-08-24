package com.ocp.communication_service.controller;

import com.ocp.communication_service.dto.request.SendMessageRequestDTO;
import com.ocp.communication_service.dto.response.MessageResponseDTO;
import com.ocp.communication_service.service.MessageService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/conversations/{conversationId}/messages")
@AllArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<MessageResponseDTO> sendMessage(
            @PathVariable UUID conversationId,
            @Valid @RequestBody SendMessageRequestDTO request) {
        MessageResponseDTO response = messageService.sendMessage(conversationId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<MessageResponseDTO>> getMessages(
            @PathVariable UUID conversationId) {
        return ResponseEntity.ok(messageService.getMessagesByConversation(conversationId));
    }
}
