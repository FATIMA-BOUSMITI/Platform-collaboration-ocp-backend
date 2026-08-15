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
    private String content;
    @NotNull(message = "L'auteur du commentaire est obligatoire")
    private UUID authorId;
}
