package com.ocp.project_management_service.security;

import java.util.UUID;

public record AuthenticatedUser(String email, UUID userId, String role) {

    public String normalizedRole() {
        String normalized = role.trim().toUpperCase();
        return normalized.startsWith("ROLE_") ? normalized.substring(5) : normalized;
    }

    public boolean isAdministratorOrDirector() {
        return normalizedRole().equals("ADMIN") || normalizedRole().equals("DIRECTOR");
    }

    public boolean isManager() {
        return normalizedRole().equals("MANAGER");
    }

    public boolean isEmployee() {
        return normalizedRole().equals("EMPLOYEE");
    }
}
