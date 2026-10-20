package com.ocp.communication_service.security;

import java.util.UUID;

public record AuthenticatedUser(String email, UUID userId, String role) {
}
