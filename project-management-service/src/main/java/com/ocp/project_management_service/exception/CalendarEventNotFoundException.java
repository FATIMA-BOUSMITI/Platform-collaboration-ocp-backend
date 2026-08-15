package com.ocp.project_management_service.exception;

public class CalendarEventNotFoundException extends RuntimeException {
    public CalendarEventNotFoundException(String id) {

        super("Event introuvable avec l'id :\" + id");
    }
}
