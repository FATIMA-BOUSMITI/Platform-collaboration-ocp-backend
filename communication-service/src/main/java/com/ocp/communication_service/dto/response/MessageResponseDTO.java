package com.ocp.communication_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@lombok.NoArgsConstructor
public class MessageResponseDTO {
    private UUID id;
    private UUID conversationId;
    private UUID senderId;
    private String content;
    private LocalDateTime sentAt;
    private String status;
    private java.util.List<UUID> readBy=java.util.List.of();
    private java.util.List<com.ocp.communication_service.service.AttachmentService.View> attachments=java.util.List.of();
    public MessageResponseDTO(UUID id,UUID conversationId,UUID senderId,String content,LocalDateTime sentAt,String status){this.id=id;this.conversationId=conversationId;this.senderId=senderId;this.content=content;this.sentAt=sentAt;this.status=status;}
}
