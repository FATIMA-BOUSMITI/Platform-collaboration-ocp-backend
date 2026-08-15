package com.ocp.project_management_service.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CommentDTO {

    private UUID id;
    private UUID authorId;
    private String content;
    private LocalDateTime createdAt;
}
