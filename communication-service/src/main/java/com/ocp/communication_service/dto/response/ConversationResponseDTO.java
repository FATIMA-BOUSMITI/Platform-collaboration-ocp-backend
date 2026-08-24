package com.ocp.communication_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ConversationResponseDTO {
    private UUID id;
    private String type;
    private String name;
    private LocalDateTime createdAt;
}
