package com.ocp.communication_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SendMessageRequestDTO {

    @NotNull(message = "L'expéditeur est obligatoire")
    private UUID senderId;

    @NotBlank(message = "Le message ne peut pas être vide")
    private String content;
}
