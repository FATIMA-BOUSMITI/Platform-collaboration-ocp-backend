package com.ocp.auth_service.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String id) {
        super("Utilisateur introuvable avec l'id : " + id);
    }
}
