package com.ocp.project_management_service.exception;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(String id) {

        super("tache introuvable avec l'id "+id);
    }
}
