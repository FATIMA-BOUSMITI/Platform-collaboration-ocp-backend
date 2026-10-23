package com.ocp.communication_service.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;
@Entity @Table(name="message_attachments") @Data
public class MessageAttachment {@Id private UUID id;@Column(nullable=false)private UUID messageId;@Column(nullable=false)private UUID conversationId;@Column(nullable=false)private String originalName;@Column(nullable=false)private String storedName;@Column(nullable=false)private String contentType;@Column(nullable=false)private long size;}
