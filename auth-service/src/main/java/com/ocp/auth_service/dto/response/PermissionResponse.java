package com.ocp.auth_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class PermissionResponse {

    private UUID id;
    private String name;
    private String description;
}
