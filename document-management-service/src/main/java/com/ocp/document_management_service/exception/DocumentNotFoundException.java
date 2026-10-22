package com.ocp.document_management_service.exception;

public class DocumentNotFoundException extends RuntimeException {
    public DocumentNotFoundException(String id) {
        super("Document introuvable avec l'id : " + id);
    }
}