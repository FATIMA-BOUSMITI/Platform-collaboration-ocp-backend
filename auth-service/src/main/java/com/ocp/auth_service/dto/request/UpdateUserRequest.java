package com.ocp.auth_service.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {

    @Email(message = "Format d'email invalide" )
    private String email ;

    private Boolean enabled;
    private Boolean accountLocked;

}
