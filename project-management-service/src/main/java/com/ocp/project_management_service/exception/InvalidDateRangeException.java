package com.ocp.project_management_service.exception;

public class InvalidDateRangeException extends RuntimeException {
    public InvalidDateRangeException() {

        super("La date de fin doit être postérieure à la date de début");

    }
}
