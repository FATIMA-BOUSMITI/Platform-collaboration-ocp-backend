package com.ocp.project_management_service.exception;


public class ProjectNotFoundException extends RuntimeException {
    public ProjectNotFoundException(String id ) {

        super("Projet introuvable avec l'id :" + id);

    }
}
