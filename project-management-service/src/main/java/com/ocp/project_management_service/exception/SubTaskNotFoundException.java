package com.ocp.project_management_service.exception;

public class SubTaskNotFoundException extends RuntimeException {
    public SubTaskNotFoundException(String id) {

        super("SubTask introuvable avec l'id :" + id);

    }
}
