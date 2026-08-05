package com.ocp.auth_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RoleUserCountResponse {
    private String roleName;
    private long userCount;
}
