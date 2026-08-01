package com.ocp.auth_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RoleUserCountRespon {
    private String roleName;
    private long userCount;
}
