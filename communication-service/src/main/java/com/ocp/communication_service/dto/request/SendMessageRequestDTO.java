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
    @jakarta.validation.constraints.Size(max=10000)
    private String content;
    @jakarta.validation.constraints.Size(max=20)
    private java.util.Set<@jakarta.validation.constraints.NotNull UUID> mentionIds=java.util.Set.of();
}
