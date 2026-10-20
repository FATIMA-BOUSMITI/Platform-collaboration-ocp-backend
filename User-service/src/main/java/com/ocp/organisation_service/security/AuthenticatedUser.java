package com.ocp.organisation_service.security;

import java.util.UUID;

public record AuthenticatedUser(String email, UUID userId, String role) {

    public boolean hasAdministratorRole() {
        String normalizedRole = role.trim().toUpperCase();
        return normalizedRole.equals("ADMIN")
            || normalizedRole.equals("ROLE_ADMIN")
            || normalizedRole.equals("ADMINISTRATOR")
            || normalizedRole.equals("ROLE_ADMINISTRATOR")
            || normalizedRole.equals("ADMINISTRATEUR")
            || normalizedRole.equals("ROLE_ADMINISTRATEUR");
    }
}
