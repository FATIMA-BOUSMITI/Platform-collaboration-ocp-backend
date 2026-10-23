package com.ocp.project_management_service.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CommentRequestDTO {

    @NotBlank(message = "Le commentaire ne peut pas être vide")
    @jakarta.validation.constraints.Size(max=5000)
    private String content;
    @jakarta.validation.constraints.Size(max=20)
    private java.util.Set<@jakarta.validation.constraints.NotNull UUID> mentionIds=java.util.Set.of();
    @NotNull(message = "L'auteur du commentaire est obligatoire")
    private UUID authorId;
}
