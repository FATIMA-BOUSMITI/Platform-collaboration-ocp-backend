package com.ocp.auth_service.exception;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(String id) {

        super("Rôle introuvable avec l'id : " + id);
    }
}
