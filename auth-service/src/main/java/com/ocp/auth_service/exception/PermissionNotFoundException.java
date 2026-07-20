package com.ocp.auth_service.exception;

public class PermissionNotFoundException extends RuntimeException {
    public PermissionNotFoundException(String id) {

        super("Permission introuvable avec l'id : " + id);
    }
}
