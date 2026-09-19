package com.ocp.communication_service.exception;

public class NotConversationMemberException extends RuntimeException {
    public NotConversationMemberException(String userId, String conversationId) {
        super("L'utilisateur " + userId + " n'est pas membre de la conversation " + conversationId);
    }
}