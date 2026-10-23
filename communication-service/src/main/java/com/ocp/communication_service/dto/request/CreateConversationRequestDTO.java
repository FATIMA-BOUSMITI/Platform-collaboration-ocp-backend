package com.ocp.communication_service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateConversationRequestDTO {

    @NotNull(message = "Le type de conversation est obligatoire")
    private String type; // PRIVATE, TEAM, PROJECT, CHANNEL

    @jakarta.validation.constraints.Size(max=150)
    private String name;
    private UUID projectId; // optionnel pour PRIVATE

    @NotNull(message = "Le créateur est obligatoire")
    private UUID creatorId;

    @NotNull(message = "La liste des membres initiaux est obligatoire")
    private List<UUID> memberIds;
}
