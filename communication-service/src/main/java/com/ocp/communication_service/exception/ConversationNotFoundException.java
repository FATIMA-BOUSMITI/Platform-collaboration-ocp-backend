package com.ocp.communication_service.exception;

public class ConversationNotFoundException extends RuntimeException {
    public ConversationNotFoundException(String id) {
        super("Conversation introuvable avec l'id : " + id);
    }
}
