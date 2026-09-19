package com.ocp.document_management_service.mapper;

import com.ocp.document_management_service.dto.response.DocumentResponseDTO;
import com.ocp.document_management_service.entity.Document;
import org.springframework.stereotype.Component;

@Component
public class DocumentMapper {

    public DocumentResponseDTO toResponse(Document document) {
        return new DocumentResponseDTO(
                document.getId(),
                document.getName(),
                document.getType().name(),
                document.getSizeBytes(),
                document.getCloudinaryUrl(),
                document.getOwnerId(),
                document.getStatus().name(),
                document.getCreatedAt()
        );
    }
}
