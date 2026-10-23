package com.ocp.communication_service.dto.response;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Data @NoArgsConstructor
public class ConversationResponseDTO {
 private UUID id; private String type; private String name; private UUID projectId; private LocalDateTime createdAt;
 private List<UUID> memberIds=List.of(); private List<UUID> adminIds=List.of();
 public ConversationResponseDTO(UUID id,String type,String name,LocalDateTime createdAt){this.id=id;this.type=type;this.name=name;this.createdAt=createdAt;}
}
