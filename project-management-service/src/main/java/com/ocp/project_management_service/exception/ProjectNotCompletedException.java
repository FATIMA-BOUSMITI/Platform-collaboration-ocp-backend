package com.ocp.project_management_service.exception;

public class ProjectNotCompletedException extends RuntimeException {
    public ProjectNotCompletedException(String id) {

        super("Le projet " + id + " ne peut pas être archivé : statut ≠ COMPLET");
    }
}
