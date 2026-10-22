package com.ocp.document_management_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class DocumentResponseDTO {
    private UUID id;
    private String name;
    private String type;
    private long sizeBytes;
    private String cloudinaryUrl;
    private UUID ownerId;
    private String status;
    private LocalDateTime createdAt;
}
