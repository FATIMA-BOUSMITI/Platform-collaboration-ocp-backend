package com.ocp.auth_service.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter

public class UserResponse {

    private UUID id;
    private String email;
    private Boolean enabled;
    private Boolean accountLocked;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
    private Set<String> roleNames;



}
