package com.ocp.project_management_service.exception;

public class InvalidStatusTransitionException extends RuntimeException {
    public InvalidStatusTransitionException() {

        super("transition de statut incohérente" );

    }
}
